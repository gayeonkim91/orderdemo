package com.example.orderdemo.support;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;

public abstract class MySqlTestContainerSupport {

    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.4.8-oraclelinux9")
            .withDatabaseName("orderdemo")
            .withUsername("app")
            .withPassword("app1234");

    static {
        mysql.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }
}
