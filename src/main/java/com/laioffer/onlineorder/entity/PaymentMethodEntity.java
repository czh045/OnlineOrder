package com.laioffer.onlineorder.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

// 支付方式只保存展示和结算所需的最少信息，绝不保存完整银行卡号。
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
