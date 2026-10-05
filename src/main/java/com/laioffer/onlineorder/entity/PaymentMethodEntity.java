package com.laioffer.onlineorder.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("payment_methods")
public record PaymentMethodEntity(
        @Id Long id,
        Long customerId,
        String cardHolder,
        String brand,
        String lastFour,
        Integer expiryMonth,
        Integer expiryYear
) {
}
