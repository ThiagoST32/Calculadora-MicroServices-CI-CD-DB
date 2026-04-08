package com.trevisan.CalculadoraMicroServicesDB.Infra;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Primary
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource primaryDataSource(){
        return DataSourceBuilder.create().build();
    }

    @Bean(name = "adminDataSource")
    @ConfigurationProperties(prefix = "admin.datasource")
    public DataSource adminDataSource(){
        return DataSourceBuilder.create().build();
    }
}
