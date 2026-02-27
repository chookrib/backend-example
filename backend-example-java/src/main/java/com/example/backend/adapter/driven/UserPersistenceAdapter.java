package com.example.backend.adapter.driven;

import com.example.backend.application.UserDto;
import com.example.backend.application.UserQueryCriteria;
import com.example.backend.application.UserQueryHandler;
import com.example.backend.application.UserQuerySort;
import com.example.backend.domain.User;
import com.example.backend.domain.UserRepository;
import com.example.backend.domain.UserUniqueSpecification;
import com.example.backend.utility.CryptoUtility;
import com.example.backend.utility.ValueUtility;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private final String tableName;

    public UserPersistenceAdapter(
            @Qualifier("sqliteJdbcTemplate") JdbcTemplate jdbcTemplate,
            @Qualifier("sqliteNamedParameterJdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        // 每次启动生成唯一表名
        this.tableName = "t_user_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // 创建表及默认管理员
        this.jdbcTemplate.execute("create table if not exists " +
                this.tableName + """
                (
                    u_id text primary key,
                    u_username text,
                    u_password text,
                    u_nickname text,
                    u_mobile text,
                    u_is_admin integer,
                    u_created_at text,
                    u_updated_at text
                )
                """);
        this.jdbcTemplate.execute("delete from " + this.tableName + " where lower(u_username) = 'admin'");
        this.jdbcTemplate.execute(String.format("insert into " +
                this.tableName + """
                    (u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at, u_updated_at)
                values
                    ('0', 'admin', '%s', '管理员', '', 1, datetime('now', 'localtime'), datetime('now', 'localtime'))
                """, CryptoUtility.md5Encode("password"))
        );
    }

    //==================================================================================================================
    // UserRepository

    /**
     * 转换成 Entity
     */
    private User toUser(SqlRowSet sqlRowSet) {
        return User.restore(
                sqlRowSet.getString("u_id"),
                sqlRowSet.getString("u_username"),
                sqlRowSet.getString("u_password"),
                sqlRowSet.getString("u_nickname"),
                sqlRowSet.getString("u_mobile"),
                sqlRowSet.getBoolean("u_is_admin"),
                ValueUtility.toDateTimeOrDefault(sqlRowSet.getString("u_created_at"), LocalDateTime.MIN),
                ValueUtility.toDateTimeOrDefault(sqlRowSet.getString("u_updated_at"), LocalDateTime.MIN)
        );
    }

    @Override
    public void insert(User entity) {
        String sql = "insert into " +
                this.tableName + """
                    (u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at, u_updated_at)
                values
                    (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getNickname(),
                entity.getMobile(),
                entity.isAdmin(),
                ValueUtility.formatDateTime(entity.getCreatedAt()),
                ValueUtility.formatDateTime(entity.getUpdatedAt())
        );
    }

    @Override
    public void update(User entity) {
        String sql = "update " +
                this.tableName + """
                set
                    u_username = ?,
                    u_password = ?,
                    u_nickname = ?,
                    u_mobile = ?,
                    u_is_admin = ?,
                    u_created_at = ?,
                    u_updated_at = ?
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
                ValueUtility.formatDateTime(entity.getUpdatedAt()),
                entity.getId()
        );
    }

    @Override
    public void deleteById(String id) {
        if (ValueUtility.isEmptyString(id))
            return;
        jdbcTemplate.update("delete from "+ this.tableName + " where u_id = ?", id);
    }

    @Override
    public User selectById(String id) {
        if (ValueUtility.isEmptyString(id))
            return null;
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from " + this.tableName + " where u_id = ?", id);
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
        String sql = "select * from " + this.tableName + " where u_id in (:ids)";
        SqlRowSet sqlRowSet = namedParameterJdbcTemplate.queryForRowSet(sql, params);
        while (sqlRowSet.next()) {
            User entity = toUser(sqlRowSet);
            list.add(entity);
        }
        return list;
    }

    @Override
    public User selectByUsername(String username) {
        if (ValueUtility.isEmptyString(username))
            return null;
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(
                "select * from " + this.tableName + " where lower(u_username) = lower(?)", username);
        if (sqlRowSet.next()) {
            return toUser(sqlRowSet);
        }
        return null;
    }

    //==================================================================================================================
    // UserUniqueSpecification

    @Override
    public boolean isUsernameUnique(String username) {
        if (ValueUtility.isEmptyString(username))
            throw new PersistenceException("参数 username 不能为空");
        return jdbcTemplate.queryForObject(
                "select count(*) from " + this.tableName + " where lower(u_username) = lower(?)", int.class, username
        ) == 0;
    }

    @Override
    public boolean isNicknameUnique(String nickname) {
        if (ValueUtility.isEmptyString(nickname))
            throw new PersistenceException("参数 nickname 不能为空");
        return jdbcTemplate.queryForObject(
                "select count(*) from " + this.tableName + " where lower(u_nickname) = lower(?)", int.class, nickname
        ) == 0;
    }

    @Override
    public boolean isMobileUnique(String mobile) {
        if (ValueUtility.isEmptyString(mobile))
            throw new PersistenceException("参数 mobile 不能为空");
        return jdbcTemplate.queryForObject(
                "select count(*) from " + this.tableName + " where lower(u_mobile) = lower(?)", int.class, mobile
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
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    /**
     * 构造查询 SQL
     */
    private String buildQueryCriteria(UserQueryCriteria criteria, Map<String, Object> paramMap) {
        if (criteria == null)
            return "";

        List<String> sqls = new ArrayList<>();
        if (!ValueUtility.isEmptyString(criteria.getKeyword())) {
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
        if (ValueUtility.isEmptyString(id))
            return null;
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from " + this.tableName + " where u_id = ?", id);
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
                "select count(*) from " + this.tableName + criteriaSql, paramMap, int.class);
    }

    @Override
    public List<UserDto> query(UserQueryCriteria criteria, UserQuerySort... sorts) {
        Map<String, Object> paramMap = new HashMap<>();
        String criteriaSql = buildQueryCriteria(criteria, paramMap);
        String sortSql = buildQuerySort(sorts);

        SqlRowSet sqlRowSet = namedParameterJdbcTemplate.queryForRowSet(
                "select * from " + this.tableName + criteriaSql + sortSql, paramMap);
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
                "select * from " + this.tableName + criteriaSql + sortSql + " limit :limitCount offset :limitOffset", paramMap);
        List<UserDto> list = new ArrayList<>();
        while (sqlRowSet.next()) {
            list.add(toUserDto(sqlRowSet));
        }
        return list;
    }
}
