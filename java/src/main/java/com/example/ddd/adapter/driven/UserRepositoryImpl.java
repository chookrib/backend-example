package com.example.ddd.adapter.driven;

import com.example.ddd.domain.User;
import com.example.ddd.domain.UserCriteria;
import com.example.ddd.domain.UserRepository;
import com.example.ddd.domain.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户Repository实现
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public UserRepositoryImpl(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    /**
     * 转换
     */
    private User toUser(SqlRowSet sqlRowSet) {
        return new User(
                sqlRowSet.getString("u_id"),
                sqlRowSet.getString("u_username"),
                sqlRowSet.getString("u_password"),
                sqlRowSet.getString("u_nickname"),
                sqlRowSet.getString("u_mobile"),
                sqlRowSet.getString("u_email"),
                sqlRowSet.getTimestamp("u_created_at").toLocalDateTime()
        );
    }

    @Override
    public void insert(User entity) {
        String sql = """
                insert into t_user
                    (u_id, u_username, u_password, u_nickname, u_mobile, u_email, u_created_at)
                values
                    (?, ?, ?, ?, ?, ?)
                """;
        jdbcTemplate.update(sql,
                entity.getId(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getNickname(),
                entity.getMobile(),
                entity.getEmail(),
                entity.getCreatedAt()
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
                    u_email = ?,
                    u_created_at = ?
                where
                    u_id = ?
                """;
        jdbcTemplate.update(sql,
                entity.getUsername(),
                entity.getPassword(),
                entity.getNickname(),
                entity.getMobile(),
                entity.getEmail(),
                entity.getCreatedAt(),
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
            throw new ValidationException("没有找到用户：" + id);
        return entity;
    }

    @Override
    public List<User> selectByIds(List<String> ids) {
        List<User> entities = new ArrayList<>();
        if (ids == null || ids.size() == 0)
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

    /**
     * 构造查询语句
     */
    private String createCriteriaSql(UserCriteria criteria, Map<String, Object> paramMap) {
        if (criteria == null)
            return "";

        List<String> sqlList = new ArrayList<>();
        if (!StringUtils.isBlank(criteria.getKeyword())) {
            sqlList.add("u_username like :keyword or u_nickname like :keyword");
            paramMap.put("keyword", "%" + criteria.getKeyword() + "%");
        }

        if (!sqlList.isEmpty())
            return StringUtils.join(sqlList, " and ");
        return "";
    }

    @Override
    public int selectCount(UserCriteria criteria) {
        Map<String, Object> paramMap = new HashMap<>();
        String criteriaSql = createCriteriaSql(criteria, paramMap);
        if (!StringUtils.isBlank(criteriaSql))
            criteriaSql = " where " + criteriaSql;

        StringBuilder sql = new StringBuilder();
        sql.append("select count(*) from t_user");
        sql.append(criteriaSql);
        return namedParameterJdbcTemplate.queryForObject(sql.toString(), paramMap, int.class);
    }

    @Override
    public List<User> select(UserCriteria criteria) {
        Map<String, Object> paramMap = new HashMap<>();
        String criteriaSql = createCriteriaSql(criteria, paramMap);
        if (!StringUtils.isBlank(criteriaSql))
            criteriaSql = " where " + criteriaSql;

        StringBuilder sql = new StringBuilder();
        sql.append("select * from t_user");
        sql.append(criteriaSql);
        sql.append(" order by fsfe_save_time desc, fsfe_id desc");

        SqlRowSet sqlRowSet = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
        List<User> list = new ArrayList<>();
        while (sqlRowSet.next()) {
            list.add(toUser(sqlRowSet));
        }
        return list;
    }

    @Override
    public List<User> selectByPage(int pageIndex, int pageSize, UserCriteria criteria) {
        Map<String, Object> paramMap = new HashMap<>();
        String criteriaSql = createCriteriaSql(criteria, paramMap);
        if (!StringUtils.isBlank(criteriaSql))
            criteriaSql = " where " + criteriaSql;

        StringBuilder sql = new StringBuilder();
        sql.append("select * from t_user");
        sql.append(criteriaSql);
        sql.append(" order by u_created_at desc, u_id desc");
        sql.append(" limit :paginationLimitBegin, :paginationLimitEnd");
        paramMap.put("paginationLimitBegin", (pageIndex - 1) * pageSize);
        paramMap.put("paginationLimitEnd", pageSize);

        SqlRowSet sqlRowSet = namedParameterJdbcTemplate.queryForRowSet(sql.toString(), paramMap);
        List<User> list = new ArrayList<>();
        while (sqlRowSet.next()) {
            list.add(toUser(sqlRowSet));
        }
        return list;
    }

    // =================================================================================================================

    @Override
    public User selectByUsername(String username) {
        SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet("select * from t_user where u_username = ?", username);
        if (sqlRowSet.next()) {
            return toUser(sqlRowSet);
        }
        return null;
    }
}
