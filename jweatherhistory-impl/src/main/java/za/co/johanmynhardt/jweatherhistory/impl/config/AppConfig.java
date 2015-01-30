package za.co.johanmynhardt.jweatherhistory.impl.config;

import com.google.common.base.Preconditions;
import com.google.common.eventbus.EventBus;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

import java.sql.SQLException;

/**
 * @author johan
 */
@Configuration
public class AppConfig {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(AppConfig.class);

    @Bean
    public PropertySourcesPlaceholderConfigurer myProperties() {
        final PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer = new PropertySourcesPlaceholderConfigurer();
        propertySourcesPlaceholderConfigurer.setLocation(new ClassPathResource("/application.properties"));
        return propertySourcesPlaceholderConfigurer;
    }

    @Bean
    public DataSource getDataSource(@Value("${dbUrl}") String dbUrl, DbBootstrapper bootstrapper) {
        Preconditions.checkArgument(!(dbUrl == null || dbUrl.trim().isEmpty()), "non-null, not-empty dbUrl expected.");

        LOG.debug("dbUrl={}", dbUrl);
        final DataSource driverManagerDataSource = new DriverManagerDataSource(dbUrl);

        try {
            bootstrapper.testDataSource(driverManagerDataSource, true);
            return driverManagerDataSource;
        } catch (SQLException e) {
            throw new RuntimeException("Unexpected database error", e);
        }
    }

    @Bean
    public DataSourceTransactionManager getTransactionManager(DataSource dataSource) {
        final DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
        LOG.debug("transactionManager={} created using dataSource={}", dataSourceTransactionManager, dataSource);
        return dataSourceTransactionManager;
    }

    @Bean
    public JdbcTemplate getJdbcTemplate(DataSource dataSource) {
        final JdbcTemplate template = new JdbcTemplate(dataSource);
        LOG.debug("jdbcTemplate={} created using dataSource={}", template, dataSource);
        return template;
    }

    @Bean
    public TransactionTemplate getTransactionTemplate(PlatformTransactionManager transactionManager) {
        final TransactionTemplate template = new TransactionTemplate(transactionManager);
        LOG.debug("transactionTemplate={} created using transactionManager={}", template, transactionManager);
        return template;
    }

    @Bean
    public EventBus getEventBus() {
        return new EventBus();
    }
}
