// repository 包放数据库访问接口。
package com.laioffer.onlineorder.repository;

// RestaurantEntity 对应 restaurants 表。
import com.laioffer.onlineorder.entity.RestaurantEntity;

// ListCrudRepository 提供基础 CRUD 方法，并让 findAll 返回 List。
import org.springframework.data.repository.ListCrudRepository;

// RestaurantRepository 负责访问 restaurants 表。
// 这里没有额外定义方法，因为默认 CRUD 已经够用了。
public interface RestaurantRepository extends ListCrudRepository<RestaurantEntity, Long> {
    // 这个接口本身虽然空，但 Spring Data JDBC 会自动为它生成实现类。
}
