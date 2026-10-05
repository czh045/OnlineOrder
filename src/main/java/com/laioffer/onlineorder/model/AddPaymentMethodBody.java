package com.laioffer.onlineorder.model;

public record AddPaymentMethodBody(
        String cardHolder,
        String cardNumber,
        Integer expiryMonth,
        Integer expiryYear
) {
}
