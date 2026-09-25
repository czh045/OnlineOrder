package com.laioffer.onlineorder.model;

import com.laioffer.onlineorder.entity.OrderLineItemEntity;

public record OrderLineItemDto(
        Long id,
        Long menuItemId,
        String menuItemName,
        Double price,
        Integer quantity
) {
    public OrderLineItemDto(OrderLineItemEntity entity) {
        this(
                entity.id(),
                entity.menuItemId(),
                entity.menuItemName(),
                entity.price(),
                entity.quantity()
        );
    }
}
