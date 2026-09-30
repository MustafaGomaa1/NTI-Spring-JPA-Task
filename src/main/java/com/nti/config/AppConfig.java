package com.nti.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@ComponentScan(basePackages = "com.nti")
@EnableTransactionManagement
public class AppConfig {

    @Bean
    public DataSource dataSource() {
        var ds = new DriverManagerDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        ds.setUrl(setting("DB_URL", "db.url",
                "jdbc:mysql://localhost:3306/shop?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true"));
        ds.setUsername(setting("DB_USERNAME", "db.username", "root"));
        ds.setPassword(setting("DB_PASSWORD", "db.password", ""));
        return ds;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource ds) {
        var emf = new LocalContainerEntityManagerFactoryBean();
        emf.setDataSource(ds);
        emf.setPackagesToScan("com.nti");
        emf.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        Properties p = new Properties();
        p.put("hibernate.hbm2ddl.auto", setting("HIBERNATE_DDL_AUTO", "hibernate.ddl-auto", "update"));
        p.put("hibernate.show_sql", setting("HIBERNATE_SHOW_SQL", "hibernate.show-sql", "false"));
        emf.setJpaProperties(p);
        return emf;
    }

    private String setting(String environmentVariable, String systemProperty, String defaultValue) {
        String property = System.getProperty(systemProperty);
        if (property != null && !property.isBlank()) {
            return property;
        }
        String environment = System.getenv(environmentVariable);
        return environment == null || environment.isBlank() ? defaultValue : environment;
    }

    @Bean
    public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
        return new JpaTransactionManager(emf);
    }

    @Bean
    public PersistenceExceptionTranslationPostProcessor translator() {
        return new PersistenceExceptionTranslationPostProcessor();
    }

}