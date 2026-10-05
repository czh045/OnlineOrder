package com.laioffer.onlineorder.runner;

import com.laioffer.onlineorder.service.CustomerService;

import org.slf4j.Logger;

import org.slf4j.LoggerFactory;

import org.springframework.boot.ApplicationArguments;

import org.springframework.boot.ApplicationRunner;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;

import org.springframework.stereotype.Component;

@Component

@ConditionalOnProperty(name = "onlineorder.dev-runner.enabled", havingValue = "true", matchIfMissing = true)

public class DevRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(DevRunner.class);

    private final CustomerService customerService;
    private final JdbcTemplate jdbcTemplate;

    public DevRunner(CustomerService customerService, JdbcTemplate jdbcTemplate) {
        this.customerService = customerService;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override

    public void run(ApplicationArguments args) {
        if (customerService.getCustomerByEmail("foo@mail.com") == null) {
            customerService.signUp("foo@mail.com", "123456", "Foo", "Bar");

            logger.info("Created demo user foo@mail.com");
        }

        jdbcTemplate.update("""
                INSERT INTO authorities (email, authority)
                SELECT ?, ?
                WHERE NOT EXISTS (
                    SELECT 1 FROM authorities WHERE email = ? AND authority = ?
                )
                """, "foo@mail.com", "ROLE_ADMIN", "foo@mail.com", "ROLE_ADMIN");
    }
}
