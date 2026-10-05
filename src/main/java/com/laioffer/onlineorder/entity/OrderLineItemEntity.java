package com.laioffer.onlineorder.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

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
