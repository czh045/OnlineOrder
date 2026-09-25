package com.laioffer.onlineorder.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// 下单时复制菜名、价格和数量，避免后来菜单改价影响历史订单。
@Table("order_line_items")
public record OrderLineItemEntity(
        @Id Long id,
        Long orderId,
        Long menuItemId,
        String menuItemName,
        Double price,
        Integer quantity
) {
}
