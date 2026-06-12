package com.example.contactmanager.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Root application context configuration: data access infrastructure and the
 * service/DAO layers. Web-specific beans live in {@link WebMvcConfig}.
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = {
        "com.example.contactmanager.dao",
        "com.example.contactmanager.service"
})
public class RootConfig {

    /**
     * Creates the embedded in-memory H2 database, initialized with the schema
     * and a small set of sample records.
     *
     * @return the application {@link DataSource}
     */
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("contactmanager")
                .addScript("classpath:db/schema.sql")
                .addScript("classpath:db/data.sql")
                .build();
    }

    /**
     * Creates the shared JDBC template used by the DAO layer.
     *
     * @param dataSource the application data source
     * @return a {@link NamedParameterJdbcTemplate} bound to the data source
     */
    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    /**
     * Creates the transaction manager backing {@code @Transactional} services.
     *
     * @param dataSource the application data source
     * @return a JDBC {@link PlatformTransactionManager}
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
