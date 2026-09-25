package com.laioffer.onlineorder.model;

// 完整卡号仅在请求处理期间用于校验并截取后四位，服务端不会把它写入数据库。
public record AddPaymentMethodBody(
        String cardHolder,
        String cardNumber,
        Integer expiryMonth,
        Integer expiryYear
) {
}
