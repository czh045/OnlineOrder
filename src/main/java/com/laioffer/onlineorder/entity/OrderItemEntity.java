// entity 包放数据库表对应的 Java 类型。
package com.laioffer.onlineorder.entity;

// @Id 标记主键。
import org.springframework.data.annotation.Id;

// @Table 指定表名。
import org.springframework.data.relational.core.mapping.Table;

// OrderItemEntity 对应 order_items 表。
// 它表示购物车里的一行商品，比如“Whopper x 2”。
@Table("order_items")
public record OrderItemEntity(
        // id 是订单项主键。
        @Id Long id,

        // menuItemId 对应 order_items.menu_item_id。
        // 它指向 menu_items.id，表示这行买的是哪道菜。
        Long menuItemId,

        // cartId 对应 order_items.cart_id。
        // 它指向 carts.id，表示这行商品属于哪个购物车。
        Long cartId,

        // price 是加入购物车时记录下来的单价。
        Double price,

        // quantity 是数量。
        Integer quantity
) {
    // record 自动生成 orderItem.menuItemId()、orderItem.quantity() 等方法。
}
