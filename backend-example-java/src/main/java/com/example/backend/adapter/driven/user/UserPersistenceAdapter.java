package com.example.backend.adapter.driven.user;

import com.example.backend.adapter.driven.DatabaseAdapter;
import com.example.backend.adapter.driven.PersistenceException;
import com.example.backend.application.QuerySortOption;
import com.example.backend.application.user.UserDto;
import com.example.backend.application.user.UserQueryCriteria;
import com.example.backend.application.user.UserQueryHandler;
import com.example.backend.application.user.UserQuerySortFields;
import com.example.backend.domain.user.User;
import com.example.backend.domain.user.UserRepository;
import com.example.backend.domain.user.UserUniqueSpecification;
import com.example.backend.utility.CryptoUtility;
import com.example.backend.utility.ValueUtility;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
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

    private final DatabaseAdapter databaseAdapter;
    private final String tableName;

    public UserPersistenceAdapter(DatabaseAdapter databaseAdapter) {
        this.databaseAdapter = databaseAdapter;
        // 每次启动生成唯一表名
        this.tableName = "t_user_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        // 创建表及默认管理员
        this.databaseAdapter.getJdbcTemplate().execute("""
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
        this.databaseAdapter.getJdbcTemplate().execute(
                "DELETE FROM %s WHERE LOWER(u_username) = 'admin'".formatted(this.tableName)
        );
        this.databaseAdapter.getJdbcTemplate().update("""
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

    // /**
    //  * 转换成 Entity
    //  */
    // private User toEntity(SqlRowSet sqlRowSet) {
    //     return User.restore(
    //             sqlRowSet.getString("u_id"),
    //             sqlRowSet.getString("u_username"),
    //             sqlRowSet.getString("u_password"),
    //             sqlRowSet.getString("u_nickname"),
    //             sqlRowSet.getString("u_mobile"),
    //             sqlRowSet.getBoolean("u_is_admin"),
    //             ValueUtility.toDateTimeOrNull(sqlRowSet.getString("u_created_at")),
    //             ValueUtility.toDateTimeOrNull(sqlRowSet.getString("u_updated_at"))
    //     );
    // }

    /**
     * 转换成 Entity
     */
    private User toEntity(ResultSet rs) {
        try {
            return User.restore(
                    rs.getString("u_id"),
                    rs.getString("u_username"),
                    rs.getString("u_password"),
                    rs.getString("u_nickname"),
                    rs.getString("u_mobile"),
                    rs.getBoolean("u_is_admin"),
                    ValueUtility.toDateTimeOrNull(rs.getString("u_created_at")),
                    ValueUtility.toDateTimeOrNull(rs.getString("u_updated_at"))
            );
        } catch (SQLException ex) {
            throw new PersistenceException("转换数据库记录为实体时发生错误", ex);
        }
    }


    @Override
    public void insert(User entity) {
        databaseAdapter.getJdbcTemplate().update("""
                        INSERT INTO %s (
                            u_id, u_username, u_password, u_nickname, u_mobile, u_is_admin, u_created_at, u_updated_at
                        ) VALUES (
                            ?, ?, ?, ?, ?, ?, ?, ?
                        )
                        """.formatted(tableName),
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
        databaseAdapter.getJdbcTemplate().update("""
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
                        """.formatted(tableName),
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
        databaseAdapter.getJdbcTemplate().update(
                "DELETE FROM %s WHERE u_id = ?".formatted(tableName), id
        );
    }

    @Override
    public User selectById(String id) {
        if (ValueUtility.isEmptyString(id))
            return null;
        // SqlRowSet sqlRowSet = databaseAdapter.getJdbcTemplate().queryForRowSet(
        //         "SELECT * FROM %s WHERE u_id = ?".formatted(tableName), id
        // );
        // if (sqlRowSet.next()) {
        //     return toEntity(sqlRowSet);
        // }
        // return null;
        List<User> list = databaseAdapter.getJdbcTemplate().query(
                "SELECT * FROM %s WHERE u_id = ?".formatted(tableName),
                (rs, rowNum) -> {
                    return toEntity(rs);
                },
                id
        );
        return DataAccessUtils.singleResult(list);
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
        // SqlRowSet sqlRowSet = databaseAdapter.getNamedParameterJdbcTemplate().queryForRowSet(
        //         "SELECT * FROM %s WHERE u_id IN (:ids)".formatted(tableName), params
        // );
        // while (sqlRowSet.next()) {
        //     User entity = toEntity(sqlRowSet);
        //     list.add(entity);
        // }
        list = databaseAdapter.getNamedParameterJdbcTemplate().query(
                "SELECT * FROM %s WHERE u_id IN (:ids)".formatted(tableName), params,
                (rs, rowNum) -> {
                    return toEntity(rs);
                }
        );
        return list;
    }

    @Override
    public User selectByUsername(String username) {
        if (ValueUtility.isEmptyString(username))
            return null;
        // SqlRowSet sqlRowSet = databaseAdapter.getJdbcTemplate().queryForRowSet(
        //         "SELECT * FROM %s WHERE LOWER(u_username) = LOWER(?)".formatted(tableName), username
        // );
        // if (sqlRowSet.next()) {
        //     return toEntity(sqlRowSet);
        // }
        List<User> list = databaseAdapter.getJdbcTemplate().query(
                "SELECT * FROM %s WHERE LOWER(u_username) = LOWER(?)".formatted(tableName),
                (rs, rowNum) -> {
                    return toEntity(rs);
                },
                username
        );
        return DataAccessUtils.singleResult(list);
    }

    //==================================================================================================================
    // UserUniqueSpecification

    @Override
    public boolean isUsernameUnique(String username) {
        if (ValueUtility.isEmptyString(username)) {
            throw new PersistenceException("参数 username 不能为空");
        }
        Integer count = databaseAdapter.getJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM %s WHERE LOWER(u_username) = LOWER(?)".formatted(tableName), Integer.class, username
        );
        return count == null || count == 0;
    }

    @Override
    public boolean isNicknameUnique(String nickname) {
        if (ValueUtility.isEmptyString(nickname)) {
            throw new PersistenceException("参数 nickname 不能为空");
        }
        Integer count = databaseAdapter.getJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM %s WHERE LOWER(u_nickname) = LOWER(?)".formatted(tableName), Integer.class, nickname
        );
        return count == null || count == 0;
    }

    @Override
    public boolean isMobileUnique(String mobile) {
        if (ValueUtility.isEmptyString(mobile)) {
            throw new PersistenceException("参数 mobile 不能为空");
        }
        Integer count = databaseAdapter.getJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM %s WHERE LOWER(u_mobile) = LOWER(?)".formatted(tableName), Integer.class, mobile
        );
        return count == null || count == 0;
    }

    //==================================================================================================================
    // UserQueryHandler

    // /**
    //  * 转换成 DTO
    //  */
    // private UserDto toDto(SqlRowSet sqlRowSet) {
    //     return new UserDto(
    //             sqlRowSet.getString("u_id"),
    //             sqlRowSet.getString("u_username"),
    //             // sqlRowSet.getString("u_password"),
    //             sqlRowSet.getString("u_nickname"),
    //             sqlRowSet.getString("u_mobile"),
    //             sqlRowSet.getBoolean("u_is_admin"),
    //             ValueUtility.toDateTimeOrNull(sqlRowSet.getString("u_created_at")),
    //             ValueUtility.toDateTimeOrNull(sqlRowSet.getString("u_updated_at"))
    //     );
    // }

    /**
     * 转换成 DTO
     */
    private UserDto toDto(ResultSet rs) {
        try {
            return new UserDto(
                    rs.getString("u_id"),
                    rs.getString("u_username"),
                    rs.getString("u_nickname"),
                    rs.getString("u_mobile"),
                    rs.getBoolean("u_is_admin"),
                    ValueUtility.toDateTimeOrNull(rs.getString("u_created_at")),
                    ValueUtility.toDateTimeOrNull(rs.getString("u_updated_at"))
            );
        } catch (SQLException ex) {
            throw new PersistenceException("转换数据库记录为 DTO 时发生错误", ex);
        }
    }

    /**
     * 构造查询 SQL
     */
    private String buildQueryCriteriaSql(UserQueryCriteria criteria, Map<String, Object> params) {
        if (criteria == null)
            return "";

        List<String> sqls = new ArrayList<>();
        if (!ValueUtility.isEmptyString(criteria.getKeyword())) {
            sqls.add("(u_username LIKE :keyword ESCAPE '\\' OR u_nickname LIKE :keyword ESCAPE '\\')");
            params.put("keyword", databaseAdapter.escapeLikePattern(criteria.getKeyword()));
        }

        if (!sqls.isEmpty())
            return " WHERE " + String.join(" AND ", sqls);
        return "";
    }

    private final static Map<String, String> SORT_FIELD_MAP = Map.of(
            UserQuerySortFields.CREATED_AT, "u_created_at",
            UserQuerySortFields.USERNAME, "u_username"
    );

    /**
     * 构造排序 SQL
     */
    private String buildQuerySortSql(List<QuerySortOption> sortOptions) {
        List<String> sqls = new ArrayList<>();

        if (sortOptions != null) {
            for (QuerySortOption sortOption : sortOptions) {
                if (SORT_FIELD_MAP.containsKey(sortOption.getField())) {
                    sqls.add(String.format("%s %s", SORT_FIELD_MAP.get(sortOption.getField()), sortOption.getOrder()));
                }
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
        // SqlRowSet sqlRowSet = databaseAdapter.getJdbcTemplate().queryForRowSet(
        //         "SELECT * FROM %s WHERE u_id = ?".formatted(tableName), id
        // );
        // if (sqlRowSet.next()) {
        //     return toDto(sqlRowSet);
        // }
        // return null;
        List<UserDto> list = databaseAdapter.getJdbcTemplate().query(
                "SELECT * FROM %s WHERE u_id = ?".formatted(tableName),
                (rs, rowNum) -> {
                    return toDto(rs);
                },
                id
        );
        return DataAccessUtils.singleResult(list);
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
        String criteriaSql = buildQueryCriteriaSql(criteria, params);
        Integer count = databaseAdapter.getNamedParameterJdbcTemplate().queryForObject(
                "SELECT COUNT(*) FROM %s %s".formatted(tableName, criteriaSql), params, Integer.class
        );
        return count == null ? 0 : count;
    }

    @Override
    public List<UserDto> query(UserQueryCriteria criteria, List<QuerySortOption> sortOptions) {
        Map<String, Object> params = new HashMap<>();
        String criteriaSql = buildQueryCriteriaSql(criteria, params);
        String sortSql = buildQuerySortSql(sortOptions);

        // SqlRowSet sqlRowSet = databaseAdapter.getNamedParameterJdbcTemplate().queryForRowSet(
        //         "SELECT * FROM %s %s %s".formatted(tableName, criteriaSql, sortSql), params
        // );
        // List<UserDto> list = new ArrayList<>();
        // while (sqlRowSet.next()) {
        //     list.add(toDto(sqlRowSet));
        // }
        // return list;
        List<UserDto> list = databaseAdapter.getNamedParameterJdbcTemplate().query(
                "SELECT * FROM %s %s %s".formatted(tableName, criteriaSql, sortSql), params,
                (rs, rowNum) -> {
                    return toDto(rs);
                }
        );
        return list;
    }

    @Override
    public List<UserDto> queryByPage(int pageNum, int pageSize, UserQueryCriteria criteria, List<QuerySortOption> sortOptions) {
        Map<String, Object> params = new HashMap<>();
        String criteriaSql = buildQueryCriteriaSql(criteria, params);
        String sortSql = buildQuerySortSql(sortOptions);

        params.put("limitCount", pageSize);
        params.put("limitOffset", (pageNum - 1) * pageSize);

        // SqlRowSet sqlRowSet = databaseAdapter.getNamedParameterJdbcTemplate().queryForRowSet(
        //         "SELECT * FROM %s %s %s LIMIT :limitCount OFFSET :limitOffset"
        //                 .formatted(tableName, criteriaSql, sortSql), params
        // );
        // List<UserDto> list = new ArrayList<>();
        // while (sqlRowSet.next()) {
        //     list.add(toDto(sqlRowSet));
        // }
        // return list;

        List<UserDto> list = databaseAdapter.getNamedParameterJdbcTemplate().query(
                "SELECT * FROM %s %s %s LIMIT :limitCount OFFSET :limitOffset"
                        .formatted(tableName, criteriaSql, sortSql), params,
                (rs, rowNum) -> {
                    return toDto(rs);
                }
        );
        return list;
    }
}
