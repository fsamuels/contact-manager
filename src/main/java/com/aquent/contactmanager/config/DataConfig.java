package com.aquent.contactmanager.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

/**
 * Data-layer Spring configuration.
 *
 * <p>Provisions an in-memory H2 database that is initialized from {@code schema.sql} and seeded
 * from {@code data.sql} on startup. The in-memory choice keeps the sample self-contained and
 * runnable with no external database, as permitted by the specification.</p>
 */
@Configuration
@EnableTransactionManagement
public class DataConfig {

    /**
     * Builds the embedded H2 {@link DataSource} and runs the schema/seed scripts.
     *
     * @return the configured data source
     */
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
                .setType(EmbeddedDatabaseType.H2)
                .setName("contactmanager")
                .addScript("classpath:schema.sql")
                .addScript("classpath:data.sql")
                .build();
    }

    /**
     * Transaction manager for JDBC-based transactions.
     *
     * @param dataSource the application data source
     * @return the transaction manager
     */
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
