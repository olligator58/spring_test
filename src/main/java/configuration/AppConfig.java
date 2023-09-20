package configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBuilder;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.Properties;

@Configuration
@PropertySource("classpath:application.properties")
@ComponentScan({"service", "dao"})
@EnableTransactionManagement
public class AppConfig {
    @Resource
    private Environment environment;

    @Bean
    public DataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }

    @Bean
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setPoolName(environment.getProperty("jdbc.poolName"));
        config.setDriverClassName(environment.getProperty("jdbc.driverClassName"));
        config.setJdbcUrl(environment.getProperty("jdbc.jdbcUrl"));
        config.setUsername(environment.getProperty("jdbc.username"));
        config.setPassword(environment.getProperty("jdbc.password"));
        return config;
    }

    @Bean
    public TransactionManager transactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

    @Bean
    public XStreamMarshaller xstreamMarshaller() {
        return new XStreamMarshaller();
    }

    @Bean
    public Properties hibernateProperties() {
        Properties properties = new Properties();
        try {
            properties.load(new ClassPathResource("hibernate.properties").getInputStream());
            return properties;
        } catch (IOException e) {
            e.printStackTrace();
            return new Properties();
        }
    }

    @Bean
    public SessionFactory sessionFactory() {
        LocalSessionFactoryBuilder builder = new LocalSessionFactoryBuilder(dataSource());
        builder.scanPackages("domain");
        builder.addProperties(hibernateProperties());
        return builder.buildSessionFactory();
    }

    @Bean
    public TransactionManager txManager() {
        HibernateTransactionManager manager = new HibernateTransactionManager();
        manager.setSessionFactory(sessionFactory());
        return manager;
    }
}
