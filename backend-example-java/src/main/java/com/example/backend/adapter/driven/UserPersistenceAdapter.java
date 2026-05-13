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

    private final SQLiteAdapter sqliteAdapter;
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final String tableName;

    public UserPersistenceAdapter(SQLiteAdapter sqLiteAdapter) {
        this.sqliteAdapter = sqLiteAdapter;
        this.jdbcTemplate = sqLiteAdapter.getJdbcTemplate();
        this.namedParameterJdbcTemplate = sqLiteAdapter.getNamedParameterJdbcTemplate();
        // 每次启动生成唯一表名
        this.tableName = "t_user_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // 创建表及默认管理员
        this.jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS %s
                (
                    u_id TEXT PRIMARY KEY,
                    u_username TEXT,
                    u_password TEXT,
                    u_nickname TEXT,
                    u_mobile TEXT,
                    u_is_admin INTEGER,
                    u_created_at TEXT,
                    u_updated_at TEXT
                )
                """.formatted(this.tableName)
        );
        this.jdbcTemplate.execute(
                "DELETE FROM %s WHERE LOWER(u_username) = 'admin'".formatted(this.tableName)
        );
        this.jdbcTemplate.update("""
                        INSERT INTO %s (
                            u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at, u_updated_at
                        ) VALUES (
                            '0', 'admin', ?, '管理员', '', 1, DATETIME('now', 'localtime'), DATETIME('now', 'localtime')
                        )
                        """.formatted(this.tableName),
                CryptoUtility.md5Encode("password")
        );
    }

    //==================================================================================================================
    // UserRepository

    /**
     * 转换成 Entity
     */
    private User toEntity(SqlRowSet sqlRowSet) {
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
        this.jdbcTemplate.update("""
                        INSERT INTO %s (
                            u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at, u_updated_at
                        ) VALUES (
                            ?, ?, ?, ?, ?, ?, ?, ?
                        )
                        """.formatted(this.tableName),
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
        this.jdbcTemplate.update("""
                        UPDATE %s
                            SET
                                u_username = ?,
                                u_password = ?,
                                u_nickname = ?,
                                u_mobile = ?,
                                u_is_admin = ?,
                                u_created_at = ?,
                                u_updated_at = ?
                            WHERE
                                u_id = ?
                        """.formatted(this.tableName),
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
        this.jdbcTemplate.update(
                "DELETE FROM %s WHERE u_id = ?".formatted(this.tableName), id
        );
    }

    @Override
    public User selectById(String id) {
        if (ValueUtility.isEmptyString(id))
            return null;
        SqlRowSet sqlRowSet = this.jdbcTemplate.queryForRowSet(
                "SELECT * FROM %s WHERE u_id = ?".formatted(this.tableName), id
        );
        if (sqlRowSet.next()) {
            return toEntity(sqlRowSet);
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
        SqlRowSet sqlRowSet = this.namedParameterJdbcTemplate.queryForRowSet(
                "SELECT * FROM %s WHERE u_id IN (:ids)".formatted(this.tableName), params
        );
        while (sqlRowSet.next()) {
            User entity = toEntity(sqlRowSet);
            list.add(entity);
        }
        return list;
    }

    @Override
    public User selectByUsername(String username) {
        if (ValueUtility.isEmptyString(username))
            return null;
        SqlRowSet sqlRowSet = this.jdbcTemplate.queryForRowSet(
                "SELECT * FROM %s WHERE LOWER(u_username) = LOWER(?)".formatted(this.tableName), username
        );
        if (sqlRowSet.next()) {
            return toEntity(sqlRowSet);
        }
        return null;
    }

    //==================================================================================================================
    // UserUniqueSpecification

    @Override
    public boolean isUsernameUnique(String username) {
        if (ValueUtility.isEmptyString(username))
            throw new PersistenceException("参数 username 不能为空");
        Integer count = this.jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM %s WHERE LOWER(u_username) = LOWER(?)".formatted(this.tableName), int.class, username
        );
        return Integer.valueOf(0).equals(count);
    }

    @Override
    public boolean isNicknameUnique(String nickname) {
        if (ValueUtility.isEmptyString(nickname))
            throw new PersistenceException("参数 nickname 不能为空");
        Integer count = this.jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM %s WHERE LOWER(u_nickname) = LOWER(?)".formatted(this.tableName), int.class, nickname
        );
        return Integer.valueOf(0).equals(count);
    }

    @Override
    public boolean isMobileUnique(String mobile) {
        if (ValueUtility.isEmptyString(mobile))
            throw new PersistenceException("参数 mobile 不能为空");
        Integer count = this.jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM %s WHERE LOWER(u_mobile) = LOWER(?)".formatted(this.tableName), int.class, mobile
        );
        return Integer.valueOf(0).equals(count);
    }

    //==================================================================================================================
    // UserQueryHandler

    /**
     * 转换成 DTO
     */
    private UserDto toDto(SqlRowSet sqlRowSet) {
        return new UserDto(
                sqlRowSet.getString("u_id"),
                sqlRowSet.getString("u_username"),
                // sqlRowSet.getString("u_password"),
                sqlRowSet.getString("u_nickname"),
                sqlRowSet.getString("u_mobile"),
                sqlRowSet.getBoolean("u_is_admin"),
                ValueUtility.toDateTimeOrDefault(sqlRowSet.getString("u_created_at"), LocalDateTime.MIN),
                ValueUtility.toDateTimeOrDefault(sqlRowSet.getString("u_updated_at"), LocalDateTime.MIN)
        );
    }

    /**
     * 构造查询 SQL
     */
    private String buildQueryCriteria(UserQueryCriteria criteria, Map<String, Object> params) {
        if (criteria == null)
            return "";

        List<String> sqls = new ArrayList<>();
        if (!ValueUtility.isEmptyString(criteria.getKeyword())) {
            sqls.add("u_username LIKE :keyword ESCAPE '\\' OR u_nickname LIKE :keyword ESCAPE '\\'");
            params.put("keyword", this.sqliteAdapter.EscapeLikePattern(criteria.getKeyword()));
        }

        if (!sqls.isEmpty())
            return " WHERE " + String.join(" AND ", sqls);
        return "";
    }

    /**
     * 构造排序 SQL
     */
    private String buildQuerySort(UserQuerySort... sorts) {
        List<String> sqls = new ArrayList<>();
        for (UserQuerySort s : sorts) {
            switch (s) {
                case CREATED_AT_ASC -> sqls.add("u_created_at ASC");
                case CREATED_AT_DESC -> sqls.add("u_created_at DESC");
                case USERNAME_ASC -> sqls.add("u_username ASC");
                case USERNAME_DESC -> sqls.add("u_username DESC");
            }
        }

        if (sqls.isEmpty())
            sqls.add("u_created_at DESC");

        sqls.add("u_id DESC");
        return " ORDER BY " + String.join(", ", sqls);
    }

    @Override
    public UserDto queryById(String id) {
        if (ValueUtility.isEmptyString(id))
            return null;
        SqlRowSet sqlRowSet = this.jdbcTemplate.queryForRowSet(
                "SELECT * FROM %s WHERE u_id = ?".formatted(this.tableName), id
        );
        if (sqlRowSet.next()) {
            return toDto(sqlRowSet);
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
        Map<String, Object> params = new HashMap<>();
        String criteriaSql = buildQueryCriteria(criteria, params);
        Integer count = this.namedParameterJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM %s %s".formatted(this.tableName, criteriaSql), params, int.class
        );
        return count == null ? 0 : count;
    }

    @Override
    public List<UserDto> query(UserQueryCriteria criteria, UserQuerySort... sorts) {
        Map<String, Object> params = new HashMap<>();
        String criteriaSql = buildQueryCriteria(criteria, params);
        String sortSql = buildQuerySort(sorts);

        SqlRowSet sqlRowSet = this.namedParameterJdbcTemplate.queryForRowSet(
                "SELECT * FROM %s %s %s".formatted(this.tableName, criteriaSql, sortSql), params
        );
        List<UserDto> list = new ArrayList<>();
        while (sqlRowSet.next()) {
            list.add(toDto(sqlRowSet));
        }
        return list;
    }

    @Override
    public List<UserDto> queryByPage(int pageNum, int pageSize, UserQueryCriteria criteria, UserQuerySort... sorts) {
        Map<String, Object> params = new HashMap<>();
        String criteriaSql = buildQueryCriteria(criteria, params);
        String sortSql = buildQuerySort(sorts);

        params.put("limitCount", pageSize);
        params.put("limitOffset", (pageNum - 1) * pageSize);

        SqlRowSet sqlRowSet = this.namedParameterJdbcTemplate.queryForRowSet(
                "SELECT * FROM %s %s %s LIMIT :limitCount OFFSET :limitOffset"
                        .formatted(this.tableName, criteriaSql, sortSql), params
        );
        List<UserDto> list = new ArrayList<>();
        while (sqlRowSet.next()) {
            list.add(toDto(sqlRowSet));
        }
        return list;
    }
}
