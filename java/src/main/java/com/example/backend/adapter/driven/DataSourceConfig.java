package com.example.backend.adapter.driven;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

/**
 * 数据源配置
 */
@Configuration
public class DataSourceConfig {

    /**
     * Sqlite DataSource
     */
    // @Primary
    @Bean(name = "sqliteDataSource")
    @ConfigurationProperties("spring.datasource.sqlite")
    public DataSource sqliteDataSource() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Sqlite JdbcTemplate
     */
    // @Primary
    @Bean(name = "sqliteJdbcTemplate")
    public JdbcTemplate sqliteJdbcTemplate(@Qualifier("sqliteDataSource") DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * Sqlite NamedParameterJdbcTemplate
     */
    // @Primary
    @Bean(name = "sqliteNamedParameterJdbcTemplate")
    public NamedParameterJdbcTemplate sqliteNamedParameterJdbcTemplate(@Qualifier("sqliteDataSource") DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }
}
