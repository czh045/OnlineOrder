// entity 包放数据库表对应的 Java 类型。
package com.laioffer.onlineorder.entity;

// @Id 用来标记主键字段。
import org.springframework.data.annotation.Id;

// @Table 用来告诉 Spring Data JDBC：这个 record 对应数据库里的哪张表。
import org.springframework.data.relational.core.mapping.Table;

// @Table("customers") 表示这个 Entity 对应 customers 表。
@Table("customers")

// record 是 Java 的一种简洁数据类写法。
// CustomerEntity 表示 customers 表里的一行数据。
public record CustomerEntity(
        // @Id 表示 id 是主键，对应 SQL 里的 id SERIAL PRIMARY KEY。
        @Id Long id,

        // email 对应 customers.email。
        // 这个项目里 email 同时也是 Spring Security 的 username。
        String email,

        // password 对应 customers.password。
        // 这里存的是加密后的密码，不应该存明文。
        String password,

        // enabled 对应 customers.enabled。
        // true 表示账号启用，false 表示账号禁用。
        boolean enabled,

        // firstName 对应数据库列 first_name。
        // Spring/Jackson 会根据命名策略处理 firstName 和 first_name 的映射。
        String firstName,

        // lastName 对应数据库列 last_name。
        String lastName
) {
    // record 会自动生成构造函数、getter 风格方法、equals、hashCode、toString。
    // 比如 customer.id()、customer.email() 都是自动生成的。
}
