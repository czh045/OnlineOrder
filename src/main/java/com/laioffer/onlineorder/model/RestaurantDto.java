// model 包放接口传输对象。
package com.laioffer.onlineorder.model;

// RestaurantEntity 是数据库餐厅表实体。
import com.laioffer.onlineorder.entity.RestaurantEntity;

// List 表示一家餐厅下面有多个菜单项。
import java.util.List;

// RestaurantDto 是返回给前端的餐厅结构。
public record RestaurantDto(
        // 餐厅 id。
        Long id,

        // 餐厅名称。
        String name,

        // 餐厅地址。
        String address,

        // 餐厅电话。
        String phone,

        // 餐厅图片地址。
        String imageUrl,

        // 这家餐厅下面的菜单列表。
        List<MenuItemDto> menuItems
) {
    // 自定义构造函数：把餐厅实体和菜单 DTO 列表组装成 RestaurantDto。
    public RestaurantDto(RestaurantEntity restaurantEntity, List<MenuItemDto> menuItems) {
        // 调用 record 主构造函数。
        this(
                // 餐厅 id。
                restaurantEntity.id(),

                // 餐厅名称。
                restaurantEntity.name(),

                // 餐厅地址。
                restaurantEntity.address(),

                // 餐厅电话。
                restaurantEntity.phone(),

                // 餐厅图片。
                restaurantEntity.imageUrl(),

                // 餐厅菜单列表。
                menuItems
        );
    }
}
