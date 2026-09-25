// entity 包放数据库表对应的 Java 类型。
package com.laioffer.onlineorder.entity;

// @Id 标记主键。
import org.springframework.data.annotation.Id;

// @Table 指定表名。
import org.springframework.data.relational.core.mapping.Table;

// MenuItemEntity 对应 menu_items 表。
@Table("menu_items")
public record MenuItemEntity(
        // id 是菜单项主键。
        @Id Long id,

        // restaurantId 对应 menu_items.restaurant_id。
        // 它是外键，表示这道菜属于哪家餐厅。
        Long restaurantId,

        // name 是菜名。
        String name,

        // description 是菜品描述。
        String description,

        // price 是菜品价格。
        Double price,

        // imageUrl 是菜品图片地址。
        String imageUrl
) {
    // record 自动生成 menuItem.id()、menuItem.price() 等访问方法。
}
