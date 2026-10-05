package com.laioffer.onlineorder.model;

import com.laioffer.onlineorder.entity.CartEntity;

import java.util.List;

public record CartDto(
        Long id,

        Long customerId,

        Double totalPrice,

        List<OrderItemDto> orderItems
) {
    public CartDto(CartEntity cartEntity, List<OrderItemDto> orderItems) {
        this(
                cartEntity.id(),

                cartEntity.customerId(),

                cartEntity.totalPrice(),

                orderItems
        );
    }
}
