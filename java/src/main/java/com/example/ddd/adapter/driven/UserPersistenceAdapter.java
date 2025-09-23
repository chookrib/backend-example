package com.example.ddd.adapter.driven;

import com.example.ddd.application.UserDto;
import com.example.ddd.application.UserQueryCriteria;
import com.example.ddd.application.UserQueryHandler;
import com.example.ddd.application.UserQuerySort;
import com.example.ddd.domain.User;
import com.example.ddd.domain.UserRepository;
import com.example.ddd.domain.UserUniqueChecker;
import com.example.ddd.utility.CryptoUtility;
import com.example.ddd.utility.ValueUtility;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户持久化Adapter
 */
@Component
public class UserPersistenceAdapter implements UserRepository, UserUniqueChecker, UserQueryHandler {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserPersistenceAdapter(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;

        // 创建表及默认管理员
        this.jdbcTemplate.execute("""
                create table if not exists t_user (u_id text primary key, u_username text, u_password text,
                u_nickname text, u_mobile text, u_is_admin integer u_created_at text)
                """);
        this.jdbcTemplate.execute("delete from t_user where lower(u_username) = 'admin'");
        this.jdbcTemplate.execute(
                "insert into t_user (u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at) " +
                "values ('0', 'admin', '" + CryptoUtility.encodeMd5("password") + "', '管理员', '', 1, datetime('now', 'localtime'))"
        );
    }

    //==================================================================================================================
    // UserRepository

    /**
     * 转换成Entity
     */
    private User toUser(SqlRowSet sqlRowSet) {
        return User.restoreUser(
                sqlRowSet.getString("u_id"),
                sqlRowSet.getString("u_username"),
                sqlRowSet.getString("u_password"),
                sqlRowSet.getString("u_nickname"),
                sqlRowSet.getString("u_mobile"),
                sqlRowSet.getBoolean("u_is_admin"),
                ValueUtility.toDateTimeReq(sqlRowSet.getString("u_created_at"))
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
                ValueUtility.toDateTimeStr(entity.getCreatedAt())
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
                ValueUtility.toDateTimeStr(entity.getCreatedAt()),
                entity.getId()
        );
    }

    @Override
    public void deleteById(String id) {
        jdbcTemplate.update("delete from t_user where u_id = ?", id);
    }

    @Override
    public User selectById(String id) {
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
            throw new RepositoryException(String.format("用户 %s 不存在", id));
        return entity;
    }

    @Override
    public List<User> selectByIds(List<String> ids) {
        List<User> entities = new ArrayList<>();
        if (ids == null || ids.isEmpty())
            return entities;

        Map<String, Object> params = new HashMap<>();
        params.put("ids", ids);
        String sql = "select * from t_user where u_id in (:ids)";
        SqlRowSet sqlRowSet = namedParameterJdbcTemplate.queryForRowSet(sql, params);
        while (sqlRowSet.next()) {
            User entity = toUser(sqlRowSet);
            entities.add(entity);
        }
        return entities;
    }

    @Override
    public User selectByUsername(String username) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from t_user where lower(u_username) = lower(?)", username);
        if (sqlRowSet.next()) {
            return toUser(sqlRowSet);
        }
        return null;
    }

    //==================================================================================================================
    // UserUniqueChecker

    @Override
    public boolean isUsernameUnique(String username) {
        User user = selectByUsername(username);
        return user == null;
    }

    @Override
    public boolean isNicknameUnique(String nickname) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from t_user where lower(u_nickname) = lower(?)", nickname);
        return !sqlRowSet.next();
    }

    @Override
    public boolean isMobileUnique(String mobile) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from t_user where lower(u_mobile) = lower(?)", mobile);
        return !sqlRowSet.next();
    }

    //==================================================================================================================
    // UserQueryHandler

    /**
     * 转换成DTO
     */
    private UserDto toUserDto(SqlRowSet sqlRowSet) {
        return new UserDto(
                sqlRowSet.getString("u_id"),
                sqlRowSet.getString("u_username"),
                //sqlRowSet.getString("u_password"),
                sqlRowSet.getString("u_nickname"),
                sqlRowSet.getString("u_mobile"),
                sqlRowSet.getBoolean("u_is_admin"),
                ValueUtility.toDateTimeReq(sqlRowSet.getString("u_created_at"))
        );
    }

    /**
     * 构造查询SQL
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
     * 构造排序SQL
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
            throw new QueryException(String.format("用户 %s 不存在", id));
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
