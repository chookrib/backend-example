package com.example.ddd.adapter.driven;

import com.example.ddd.application.UserDto;
import com.example.ddd.application.UserQueryCriteria;
import com.example.ddd.application.UserQueryHandler;
import com.example.ddd.application.UserQuerySort;
import com.example.ddd.domain.User;
import com.example.ddd.domain.UserRepository;
import com.example.ddd.domain.UserUniqueSpecification;
import com.example.ddd.utility.ValueUtility;
import com.example.ddd.utility.CryptoUtility;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户持久化 Adapter
 */
@Component
public class UserPersistenceAdapter implements UserRepository, UserUniqueSpecification, UserQueryHandler {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserPersistenceAdapter(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;

        // 创建表及默认管理员
        this.jdbcTemplate.execute("""
                create table if not exists t_user
                (
                    u_id text primary key,
                    u_username text,
                    u_password text,
                    u_nickname text,
                    u_mobile text,
                    u_is_admin integer,
                    u_created_at text
                )
                """);
        this.jdbcTemplate.execute("delete from t_user where lower(u_username) = 'admin'");
        this.jdbcTemplate.execute(String.format("""
                insert into t_user
                    (u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at)
                values
                    ('0', 'admin', '%s', '管理员', '', 1, datetime('now', 'localtime'))
                """, CryptoUtility.encodeMd5("password"))
        );
    }

    //==================================================================================================================
    // UserRepository

    /**
     * 转换成 Entity
     */
    private User toUser(SqlRowSet sqlRowSet) {
        return User.restoreUser(
                sqlRowSet.getString("u_id"),
                sqlRowSet.getString("u_username"),
                sqlRowSet.getString("u_password"),
                sqlRowSet.getString("u_nickname"),
                sqlRowSet.getString("u_mobile"),
                sqlRowSet.getBoolean("u_is_admin"),
                ValueUtility.toDateTimeOrDefault(sqlRowSet.getString("u_created_at"), LocalDateTime.MIN)
        );
    }

    @Override
    public void insert(User entity) {
        String sql = """
                insert into t_user
                    (u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at)
                values
                    (?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getNickname(),
                entity.getMobile(),
                entity.isAdmin(),
                ValueUtility.formatDateTime(entity.getCreatedAt())
        );
    }

    @Override
    public void update(User entity) {
        String sql = """
                update t_user
                set
                    u_username = ?,
                    u_password = ?,
                    u_nickname = ?,
                    u_mobile = ?,
                    u_is_admin = ?,
                    u_created_at = ?
                where
                    u_id = ?
                """;
        jdbcTemplate.update(sql,
                entity.getUsername(),
                entity.getPassword(),
                entity.getNickname(),
                entity.getMobile(),
                entity.isAdmin(),
                ValueUtility.formatDateTime(entity.getCreatedAt()),
                entity.getId()
        );
    }

    @Override
    public void deleteById(String id) {
        if (ValueUtility.isBlank(id))
            return;
        jdbcTemplate.update("delete from t_user where u_id = ?", id);
    }

    @Override
    public User selectById(String id) {
        if (ValueUtility.isBlank(id))
            return null;
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from t_user where u_id = ?", id);
        if (sqlRowSet.next()) {
            return toUser(sqlRowSet);
        }
        return null;
    }

    @Override
    public User selectByIdReq(String id) {
        User entity = selectById(id);
        if (entity == null)
            throw new PersistenceException(String.format("用户 %s 不存在", id));
        return entity;
    }

    @Override
    public List<User> selectByIds(List<String> ids) {
        List<User> list = new ArrayList<>();
        if (ids == null || ids.isEmpty())
            return list;

        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        String sql = "select * from t_user where u_id in (:ids)";
        SqlRowSet sqlRowSet = namedParameterJdbcTemplate.queryForRowSet(sql, params);
        while (sqlRowSet.next()) {
            User entity = toUser(sqlRowSet);
            list.add(entity);
        }
        return list;
    }

    @Override
    public User selectByUsername(String username) {
        if (ValueUtility.isBlank(username))
            return null;
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(
                "select * from t_user where lower(u_username) = lower(?)", username);
        if (sqlRowSet.next()) {
            return toUser(sqlRowSet);
        }
        return null;
    }

    //==================================================================================================================
    // UserUniqueSpecification

    @Override
    public boolean isUsernameUnique(String username) {
        if (ValueUtility.isBlank(username))
            throw new PersistenceException("参数 username 不能为空");
        return jdbcTemplate.queryForObject(
                "select count(*) from t_user where lower(u_username) = lower(?)", int.class, username
        ) == 0;
    }

    @Override
    public boolean isNicknameUnique(String nickname) {
        if (ValueUtility.isBlank(nickname))
            throw new PersistenceException("参数 nickname 不能为空");
        return jdbcTemplate.queryForObject(
                "select count(*) from t_user where lower(u_nickname) = lower(?)", int.class, nickname
        ) == 0;
    }

    @Override
    public boolean isMobileUnique(String mobile) {
        if (ValueUtility.isBlank(mobile))
            throw new PersistenceException("参数 mobile 不能为空");
        return jdbcTemplate.queryForObject(
                "select * from t_user where lower(u_mobile) = lower(?)", int.class, mobile
        ) == 0;
    }

    //==================================================================================================================
    // UserQueryHandler

    /**
     * 转换成 DTO
     */
    private UserDto toUserDto(SqlRowSet sqlRowSet) {
        User user = toUser(sqlRowSet);
        return new UserDto(
                user.getId(),
                user.getUsername(),
                // user.getPassword(),
                user.getNickname(),
                user.getMobile(),
                user.isAdmin(),
                user.getCreatedAt()
        );
    }

    /**
     * 构造查询 SQL
     */
    private String buildQueryCriteria(UserQueryCriteria criteria, Map<String, Object> paramMap) {
        if (criteria == null)
            return "";

        List<String> sqls = new ArrayList<>();
        if (!ValueUtility.isBlank(criteria.getKeyword())) {
            sqls.add("u_username like :keyword or u_nickname like :keyword");
            paramMap.put("keyword", "%" + criteria.getKeyword() + "%");
        }

        if (!sqls.isEmpty())
            return " where " + String.join(" and ", sqls);
        return "";
    }

    /**
     * 构造排序 SQL
     */
    private String buildQuerySort(UserQuerySort... sorts) {
        List<String> sqls = new ArrayList<>();
        for (UserQuerySort s : sorts) {
            switch (s) {
                case CREATED_AT_ASC -> sqls.add("u_created_at asc");
                case CREATED_AT_DESC -> sqls.add("u_created_at desc");
                case USERNAME_ASC -> sqls.add("u_username asc");
                case USERNAME_DESC -> sqls.add("u_username desc");
            }
        }

        if (sqls.isEmpty())
            sqls.add("u_created_at desc");

        sqls.add("u_id desc");
        return " order by " + String.join(", ", sqls);
    }

    @Override
    public UserDto queryById(String id) {
        if (ValueUtility.isBlank(id))
            return null;
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from t_user where u_id = ?", id);
        if (sqlRowSet.next()) {
            return toUserDto(sqlRowSet);
        }
        return null;
    }

    @Override
    public UserDto queryByIdReq(String id) {
        UserDto dto = queryById(id);
        if (dto == null)
            throw new PersistenceException(String.format("用户 %s 不存在", id));
        return dto;
    }

    @Override
    public int queryCount(UserQueryCriteria criteria) {
        Map<String, Object> paramMap = new HashMap<>();
        String criteriaSql = buildQueryCriteria(criteria, paramMap);
        return namedParameterJdbcTemplate.queryForObject(
                "select count(*) from t_user" + criteriaSql, paramMap, int.class);
    }

    @Override
    public List<UserDto> query(UserQueryCriteria criteria, UserQuerySort... sorts) {
        Map<String, Object> paramMap = new HashMap<>();
        String criteriaSql = buildQueryCriteria(criteria, paramMap);
        String sortSql = buildQuerySort(sorts);

        SqlRowSet sqlRowSet = namedParameterJdbcTemplate.queryForRowSet(
                "select * from t_user" + criteriaSql + sortSql, paramMap);
        List<UserDto> list = new ArrayList<>();
        while (sqlRowSet.next()) {
            list.add(toUserDto(sqlRowSet));
        }
        return list;
    }

    @Override
    public List<UserDto> queryByPage(int pageNum, int pageSize, UserQueryCriteria criteria, UserQuerySort... sorts) {
        Map<String, Object> paramMap = new HashMap<>();
        String criteriaSql = buildQueryCriteria(criteria, paramMap);
        String sortSql = buildQuerySort(sorts);

        paramMap.put("limitCount", pageSize);
        paramMap.put("limitOffset", (pageNum - 1) * pageSize);

        SqlRowSet sqlRowSet = namedParameterJdbcTemplate.queryForRowSet(
                "select * from t_user" + criteriaSql + sortSql + " limit :limitCount offset :limitOffset", paramMap);
        List<UserDto> list = new ArrayList<>();
        while (sqlRowSet.next()) {
            list.add(toUserDto(sqlRowSet));
        }
        return list;
    }
}
