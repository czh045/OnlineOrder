package com.laioffer.onlineorder.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

// orders 是已经完成结算的订单主表，不要和购物车中的 order_items 混在一起。
@Table("orders")
public record OrderEntity(
        @Id Long id,
        Long customerId,
        Long paymentMethodId,
        Double totalPrice,
        String status,
        Instant createdAt
) {
}
