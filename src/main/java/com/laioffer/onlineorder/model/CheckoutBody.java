package com.laioffer.onlineorder.model;

// 结算时前端只提交用户已经保存过的支付方式 ID。
public record CheckoutBody(Long paymentMethodId) {
}
