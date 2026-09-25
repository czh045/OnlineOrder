// repository 包放数据库访问接口。
package com.laioffer.onlineorder.repository;

// MenuItemEntity 对应 menu_items 表。
import com.laioffer.onlineorder.entity.MenuItemEntity;

// ListCrudRepository 提供基础 CRUD 方法，并且 findAll 返回 List。
import org.springframework.data.repository.ListCrudRepository;

// List 表示返回多个菜单项。
import java.util.List;

// MenuItemRepository 负责访问 menu_items 表。
// ListCrudRepository<MenuItemEntity, Long> 表示主键类型是 Long。
public interface MenuItemRepository extends ListCrudRepository<MenuItemEntity, Long> {

    // 根据 restaurantId 查询菜单项。
    // Spring Data JDBC 根据方法名生成：
    // SELECT * FROM menu_items WHERE restaurant_id = ?
    List<MenuItemEntity> getByRestaurantId(Long restaurantId);
}
