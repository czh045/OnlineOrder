package com.laioffer.onlineorder.model;

import com.laioffer.onlineorder.entity.PaymentMethodEntity;

public record PaymentMethodDto(
        Long id,
        String cardHolder,
        String brand,
        String lastFour,
        Integer expiryMonth,
        Integer expiryYear
) {
    public PaymentMethodDto(PaymentMethodEntity entity) {
        this(
                entity.id(),
                entity.cardHolder(),
                entity.brand(),
                entity.lastFour(),
                entity.expiryMonth(),
                entity.expiryYear()
        );
    }
}
