// model 包放接口传输对象。
package com.laioffer.onlineorder.model;

// MenuItemEntity 是菜单表实体。
import com.laioffer.onlineorder.entity.MenuItemEntity;

// OrderItemEntity 是订单项表实体。
import com.laioffer.onlineorder.entity.OrderItemEntity;

// OrderItemDto 是购物车接口里每一行商品的响应结构。
public record OrderItemDto(
        // 订单项 id，对应 order_items.id。
        Long id,

        // 菜品 id，对应 order_items.menu_item_id。
        // 前端点击“加入购物车”时传的是 menu_id，后端保存到这里。
        Long menuItemId,

        // 餐厅 id，对应 menu_items.restaurant_id。
        // 购物车里展示商品时，有时也需要知道它属于哪家餐厅。
        Long restaurantId,

        // 加入购物车时记录的单价。
        Double price,

        // 购买数量。
        Integer quantity,

        // 菜名。
        // 因为 application.yaml 里配置了 SNAKE_CASE，
        // 这个 Java 字段返回给前端时会变成 menu_item_name。
        String menuItemName,

        // 菜品描述。
        // 返回给前端时会变成 menu_item_description。
        String menuItemDescription,

        // 菜品图片地址。
        // 返回给前端时会变成 menu_item_image_url。
        String menuItemImageUrl
) {
    // 自定义构造函数：把 OrderItemEntity 和 MenuItemEntity 组合成一个 DTO。
    public OrderItemDto(OrderItemEntity orderItemEntity, MenuItemEntity menuItemEntity) {
        // 调用 record 主构造函数，给所有字段赋值。
        this(
                // 订单项 id。
                orderItemEntity.id(),

                // 订单项里保存的菜品 id。
                orderItemEntity.menuItemId(),

                // 菜品所属餐厅 id 来自 menu_items 表。
                menuItemEntity.restaurantId(),

                // 订单项里记录的价格。
                orderItemEntity.price(),

                // 订单项里的数量。
                orderItemEntity.quantity(),

                // 菜名来自 menu_items 表。
                menuItemEntity.name(),

                // 菜品描述来自 menu_items 表。
                menuItemEntity.description(),

                // 菜品图片地址来自 menu_items 表。
                menuItemEntity.imageUrl()
        );
    }
}
