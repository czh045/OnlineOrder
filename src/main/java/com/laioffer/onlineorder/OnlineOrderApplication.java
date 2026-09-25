// package 表示这个类属于哪个“包”。包名通常和文件夹路径一致：
// src/main/java/com/laioffer/onlineorder/OnlineOrderApplication.java
// 对应 package com.laioffer.onlineorder。
package com.laioffer.onlineorder;

// SpringApplication 是 Spring Boot 提供的启动工具类。
// 你调用它的 run 方法，Spring Boot 就会创建整个应用。
import org.springframework.boot.SpringApplication;

// SpringBootApplication 是最核心的启动注解。
// 它会告诉 Spring：从这个类所在的包开始扫描 Controller、Service、Repository、Configuration 等组件。
import org.springframework.boot.autoconfigure.SpringBootApplication;

// EnableCaching 用来打开 Spring 的缓存功能。
// 项目里的 @Cacheable、@CacheEvict 只有在这里启用缓存后才会生效。
import org.springframework.cache.annotation.EnableCaching;

// 打开缓存功能。这个项目里 RestaurantService 和 CartService 都用到了缓存注解。
@EnableCaching

// 标记这是一个 Spring Boot 应用的启动类。
@SpringBootApplication

// Java 里的 public class 表示定义一个公开类。
// 类名必须和文件名一致，所以这里叫 OnlineOrderApplication。
public class OnlineOrderApplication {

    // main 方法是普通 Java 程序的入口。
    // 你在 VS Code 点 F5 或运行 gradlew bootRun，本质上最后都会执行这个方法。
    public static void main(String[] args) {
        // SpringApplication.run 会做很多事：
        // 1. 创建 Spring 容器；
        // 2. 扫描 com.laioffer.onlineorder 包下面的组件；
        // 3. 启动内置 Tomcat；
        // 4. 读取 application.yaml；
        // 5. 连接 PostgreSQL；
        // 6. 初始化 Spring Security、Controller、Service、Repository。
        SpringApplication.run(OnlineOrderApplication.class, args);
    }

}
