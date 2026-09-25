// 这个类在 com.laioffer.onlineorder 包下面。
// 因为启动类 OnlineOrderApplication 也在这个包，所以 Spring Boot 会自动扫描到它。
package com.laioffer.onlineorder;

// PathRequest 是 Spring Boot Security 提供的工具。
// 它可以帮我们一次性匹配常见静态资源路径，比如 CSS、JS、图片。
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;

// Bean 表示“把这个方法返回的对象交给 Spring 管理”。
import org.springframework.context.annotation.Bean;

// Configuration 表示这是一个配置类，不是普通业务类。
import org.springframework.context.annotation.Configuration;

// HttpMethod 用来指定 GET、POST、DELETE 这种 HTTP 方法。
import org.springframework.http.HttpMethod;

// HttpStatus 表示 HTTP 状态码，比如 200 OK、401 UNAUTHORIZED。
import org.springframework.http.HttpStatus;

// HttpSecurity 是 Spring Security 的核心配置对象。
// 我们用它来配置哪些接口需要登录，登录成功/失败怎么返回。
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

// PasswordEncoderFactories 可以创建一套默认的密码加密器。
import org.springframework.security.crypto.factory.PasswordEncoderFactories;

// PasswordEncoder 是 Spring Security 里负责“加密/校验密码”的接口。
import org.springframework.security.crypto.password.PasswordEncoder;

// JdbcUserDetailsManager 是 Spring Security 提供的 JDBC 用户管理器。
// 它会把用户信息存进数据库，而不是存在内存里。
import org.springframework.security.provisioning.JdbcUserDetailsManager;

// UserDetailsManager 是 Spring Security 管理用户的抽象接口。
// 代码里用接口类型，后面可以替换实现类。
import org.springframework.security.provisioning.UserDetailsManager;

// SecurityFilterChain 表示一组安全过滤规则。
// 请求进 Controller 前，会先经过这条过滤链。
import org.springframework.security.web.SecurityFilterChain;

// HttpStatusEntryPoint 可以让未登录请求直接返回某个 HTTP 状态码。
import org.springframework.security.web.authentication.HttpStatusEntryPoint;

// SimpleUrlAuthenticationFailureHandler 是表单登录失败时用的处理器。
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

// HttpStatusReturningLogoutSuccessHandler 表示登出成功后直接返回状态码。
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

// DataSource 是数据库连接源。
// Spring Boot 会根据 application.yaml 自动创建它。
import javax.sql.DataSource;

// @Configuration 告诉 Spring：这个类里面有一些配置 Bean，需要启动时加载。
@Configuration

// AppConfig 是整个项目的安全配置类。
// 你可以把它理解成“登录、注册、权限、密码加密”的总开关。
public class AppConfig {

    // @Bean 表示这个方法创建出来的对象会被放进 Spring 容器。
    // 之后其他类要用 UserDetailsManager，Spring 就会把这里的对象注入过去。
    @Bean

    // users 方法负责告诉 Spring Security：
    // 用户账号、密码、权限要从 PostgreSQL 数据库里读写。
    UserDetailsManager users(DataSource dataSource) {
        // dataSource 是 Spring 自动传进来的数据库连接对象。
        // new JdbcUserDetailsManager(dataSource) 的意思是：
        // 用这个数据库连接去管理用户。
        JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager(dataSource);

        // 注册新用户时，Spring Security 会执行这条 SQL。
        // 三个问号分别对应：邮箱、加密后的密码、是否启用。
        // 这里把 Spring Security 默认的 username 改成了我们项目里的 email。
        userDetailsManager.setCreateUserSql("INSERT INTO customers (email, password, enabled) VALUES (?, ?, ?)");

        // 创建用户角色时，Spring Security 会执行这条 SQL。
        // authority 通常长得像 ROLE_USER。
        userDetailsManager.setCreateAuthoritySql("INSERT INTO authorities (email, authority) VALUES (?, ?)");

        // 用户登录时，Spring Security 会用这条 SQL 根据邮箱查账号。
        // 返回的三列必须是：用户名、密码、enabled。
        userDetailsManager.setUsersByUsernameQuery("SELECT email, password, enabled FROM customers WHERE email = ?");

        // 用户登录时，Spring Security 还会用这条 SQL 查权限。
        // 没有权限记录的话，用户即使密码对了也可能无法完成认证。
        userDetailsManager.setAuthoritiesByUsernameQuery("SELECT email, authority FROM authorities WHERE email = ?");

        // 把配置好的 JDBC 用户管理器交给 Spring 使用。
        return userDetailsManager;
    }

    // @Bean：把 PasswordEncoder 注册进 Spring 容器。
    @Bean

    // passwordEncoder 方法负责创建“密码加密器”。
    PasswordEncoder passwordEncoder() {
        // 密码不能明文存数据库。
        // createDelegatingPasswordEncoder 会创建一个默认安全的编码器。
        // 注册时：把原始密码变成哈希字符串。
        // 登录时：把用户输入的密码和数据库里的哈希做匹配。
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // @Bean：把这条安全过滤链注册给 Spring Security。
    @Bean

    // filterChain 方法定义整个项目的访问规则。
    // throws Exception 是因为 Spring Security 配置过程中可能抛出异常。
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // http 是安全配置对象。
        // 后面这一长串是链式调用：每调用一次，就继续返回 http，让你接着配置。
        http

                // 关闭 CSRF 防护。
                // 现在这个项目主要是课程练习和前后端接口联调，所以先关掉。
                // 商用项目里要根据登录方式重新认真配置。
                .csrf(csrf -> csrf.disable())

                // 开始配置“请求授权规则”。
                // auth 是授权配置对象。
                .authorizeHttpRequests(auth -> auth

                        // 放行常见静态资源。
                        // 比如 React 打包后的 JS、CSS、图片，不登录也能访问。
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

                        // 放行首页、健康检查、静态 JSON、图片和 /static 目录。
                        // 这样浏览器打开网页时，不会因为没登录就连页面文件都拿不到。
                        .requestMatchers(HttpMethod.GET, "/", "/index.html", "/health", "/*.json", "/*.png", "/static/**").permitAll()

                        // 放行登录、登出、注册。
                        // 因为用户没登录之前也必须能调用这些接口。
                        .requestMatchers(HttpMethod.POST, "/login", "/logout", "/signup").permitAll()

                        // 放行餐厅和菜单查询。
                        // 未登录用户也可以浏览餐厅和菜单，只有购物车相关接口需要登录。
                        .requestMatchers(HttpMethod.GET, "/restaurants/**", "/restaurant/**").permitAll()

                        // 菜单和餐厅的写操作只属于管理员；即使有人手动构造请求也会被后端拒绝。
                        .requestMatchers(HttpMethod.POST, "/restaurants", "/restaurant/*/menu").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/restaurant/**", "/menu/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/restaurant/**", "/menu/**").hasRole("ADMIN")

                        // 除了上面明确放行的接口，其他请求都必须登录。
                        // 比如 GET /cart、POST /cart、POST /cart/checkout。
                        .anyRequest().authenticated()
                )

                // 配置认证失败时怎么处理。
                .exceptionHandling(exception -> exception

                        // 如果用户没登录就访问受保护接口，直接返回 401。
                        // 401 的意思是 Unauthorized，也就是“你还没通过身份认证”。
                        .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
                )

                // 开启表单登录。
                // Spring Security 默认会处理 POST /login。
                .formLogin(form -> form

                        // 登录成功后不要跳转页面，直接返回 200 OK。
                        // 这对前后端分离/React 前端更友好。
                        .successHandler((request, response, authentication) -> response.setStatus(HttpStatus.OK.value()))

                        // 登录失败时使用默认失败处理器。
                        // 通常会返回 401 或相关错误响应。
                        .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                )

                // 配置登出逻辑。
                // Spring Security 默认会处理 POST /logout。
                .logout(logout -> logout

                        // 登出成功后直接返回 200 OK，不跳转页面。
                        .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK))
                );

        // http.build() 会把上面的配置真正构建成 SecurityFilterChain。
        // Spring Security 之后就按这条过滤链处理所有 HTTP 请求。
        return http.build();
    }
}
