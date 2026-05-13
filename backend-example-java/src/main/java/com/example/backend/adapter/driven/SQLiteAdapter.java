package com.example.backend.adapter.driven;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * SQLite 数据库 Adapter
 */
@Component
public class SQLiteAdapter {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public SQLiteAdapter(
            @Qualifier("sqliteJdbcTemplate") JdbcTemplate jdbcTemplate,
            @Qualifier("sqliteNamedParameterJdbcTemplate") NamedParameterJdbcTemplate namedParameterJdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return this.namedParameterJdbcTemplate;
    }

    /**
     * 将 % 和 _ 转义为 \% 和 \_
     * LIKE 查询指定 ESCAPE '\'  \ 本身也需要转义
     */
    public String EscapeLikePattern(String pattern) {
        pattern = pattern.replace("\\", "\\\\");
        pattern = pattern.replace("%", "\\%");
        pattern = pattern.replace("_", "\\_");
        return String.format("%%%s%%", pattern);
    }
}
