// runner 包放“程序启动后自动执行”的辅助代码。
package com.laioffer.onlineorder.runner;

// CustomerService 用来注册和查询用户。
import com.laioffer.onlineorder.service.CustomerService;

// Logger 是日志接口，用来在控制台输出信息。
import org.slf4j.Logger;

// LoggerFactory 用来创建 Logger。
import org.slf4j.LoggerFactory;

// ApplicationArguments 表示启动程序时传入的参数。
import org.springframework.boot.ApplicationArguments;

// ApplicationRunner 是 Spring Boot 提供的接口。
// 实现它以后，run 方法会在应用启动完成后自动执行。
import org.springframework.boot.ApplicationRunner;

// ConditionalOnProperty 表示“满足某个配置条件时才创建这个组件”。
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;

// Component 表示这是普通 Spring 组件。
import org.springframework.stereotype.Component;

// @Component 让 Spring 扫描并创建这个对象。
@Component

// 这个注解决定 DevRunner 是否启用。
// onlineorder.dev-runner.enabled=true 时启用。
// matchIfMissing=true 表示如果配置里没写这个开关，也默认启用。
@ConditionalOnProperty(name = "onlineorder.dev-runner.enabled", havingValue = "true", matchIfMissing = true)

// DevRunner 用来在开发环境启动时自动创建演示用户。
public class DevRunner implements ApplicationRunner {

    // 创建日志对象。
    // static final 表示它属于类本身，而且不会被重新赋值。
    private static final Logger logger = LoggerFactory.getLogger(DevRunner.class);

    // 保存 CustomerService，用它查询和注册用户。
    private final CustomerService customerService;
    private final JdbcTemplate jdbcTemplate;

    // 构造函数注入 CustomerService。
    public DevRunner(CustomerService customerService, JdbcTemplate jdbcTemplate) {
        // 把 Spring 传进来的 CustomerService 保存下来。
        this.customerService = customerService;
        this.jdbcTemplate = jdbcTemplate;
    }

    // @Override 表示这个方法来自 ApplicationRunner 接口。
    @Override

    // run 方法会在 Spring Boot 应用启动完成后执行。
    // 它不是处理 HTTP 请求的接口，而是启动后的初始化逻辑。
    public void run(ApplicationArguments args) {
        // 先查数据库里有没有 foo@mail.com 这个演示用户。
        if (customerService.getCustomerByEmail("foo@mail.com") == null) {
            // 如果没有，就创建一个演示用户。
            // 邮箱：foo@mail.com
            // 密码：123456
            // 姓名：Foo Bar
            customerService.signUp("foo@mail.com", "123456", "Foo", "Bar");

            // 在控制台打印一条日志，说明 demo 用户已创建。
            logger.info("Created demo user foo@mail.com");
        }

        // 用开发账号演示管理员页面。WHERE NOT EXISTS 让这个 Runner 在不重置数据库时也可安全重复启动。
        jdbcTemplate.update("""
                INSERT INTO authorities (email, authority)
                SELECT ?, ?
                WHERE NOT EXISTS (
                    SELECT 1 FROM authorities WHERE email = ? AND authority = ?
                )
                """, "foo@mail.com", "ROLE_ADMIN", "foo@mail.com", "ROLE_ADMIN");
    }
}
