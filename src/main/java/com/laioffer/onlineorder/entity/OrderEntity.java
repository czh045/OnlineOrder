package com.laioffer.onlineorder.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

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
