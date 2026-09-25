// repository 包放数据访问层代码。
// Repository 的职责是跟数据库打交道。
package com.laioffer.onlineorder.repository;

// CustomerEntity 对应 customers 表。
import com.laioffer.onlineorder.entity.CustomerEntity;

// @Modifying 表示这个 SQL 会修改数据，比如 UPDATE 或 DELETE。
import org.springframework.data.jdbc.repository.query.Modifying;

// @Query 用来手写 SQL。
import org.springframework.data.jdbc.repository.query.Query;

// ListCrudRepository 是 Spring Data 提供的 Repository 接口。
// 它自带 findAll、findById、save、deleteById 等基础方法。
import org.springframework.data.repository.ListCrudRepository;

// @Param 用来把 Java 参数绑定到 SQL 里的命名参数。
import org.springframework.data.repository.query.Param;

// List 表示查询结果可能有多条。
import java.util.List;

// CustomerRepository 是 customers 表的数据访问接口。
// interface 表示这里只定义方法，不手写具体实现。
// Spring Data JDBC 会在运行时自动生成实现类。
public interface CustomerRepository extends ListCrudRepository<CustomerEntity, Long> {

    // extends ListCrudRepository<CustomerEntity, Long> 的意思是：
    // 1. 这个 Repository 管理 CustomerEntity；
    // 2. CustomerEntity 的主键类型是 Long。

    // 根据 firstName 查询用户。
    // Spring Data JDBC 会根据方法名 findByFirstName 自动生成类似：
    // SELECT * FROM customers WHERE first_name = ?
    List<CustomerEntity> findByFirstName(String firstName);

    // 根据 lastName 查询用户。
    // 方法名里的 LastName 会对应 CustomerEntity.lastName，也就是数据库列 last_name。
    List<CustomerEntity> findByLastName(String lastName);

    // 根据 email 查询单个用户。
    // 因为 database-init.sql 里 email 是 UNIQUE，所以这里返回一个 CustomerEntity，而不是 List。
    CustomerEntity findByEmail(String email);

    // @Modifying 告诉 Spring：下面这条 @Query 不是 SELECT，而是会修改数据库。
    @Modifying

    // @Query 里写的是自定义 SQL。
    // 三个冒号参数 :firstName、:lastName、:email 会从方法参数里绑定。
    @Query("""
            UPDATE customers
            SET first_name = :firstName, last_name = :lastName
            WHERE email = :email
            """)

    // 根据邮箱更新用户姓名。
    // 注册时 Spring Security 先创建 email/password/enabled，
    // 然后 CustomerService 再调用这个方法补 first_name 和 last_name。
    void updateNameByEmail(
            // @Param("email") 把 Java 参数 email 绑定到 SQL 里的 :email。
            @Param("email") String email,

            // 绑定到 SQL 里的 :firstName。
            @Param("firstName") String firstName,

            // 绑定到 SQL 里的 :lastName。
            @Param("lastName") String lastName
    );
}
