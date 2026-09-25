// model 包放接口传输对象。
package com.laioffer.onlineorder.model;

// MenuItemEntity 是数据库菜单表实体。
import com.laioffer.onlineorder.entity.MenuItemEntity;

// MenuItemDto 是返回给前端的菜单项结构。
public record MenuItemDto(
        // 菜品 id。
        Long id,

        // 所属餐厅 id。
        Long restaurantId,

        // 菜名。
        String name,

        // 菜品描述。
        String description,

        // 价格。
        Double price,

        // 图片地址。
        String imageUrl
) {
    // 自定义构造函数：把数据库实体转换成接口响应对象。
    public MenuItemDto(MenuItemEntity menuItemEntity) {
        // 调用 record 主构造函数。
        this(
                // 菜品 id。
                menuItemEntity.id(),

                // 餐厅 id。
                menuItemEntity.restaurantId(),

                // 菜名。
                menuItemEntity.name(),

                // 描述。
                menuItemEntity.description(),

                // 价格。
                menuItemEntity.price(),

                // 图片 URL。
                menuItemEntity.imageUrl()
        );
    }
}
