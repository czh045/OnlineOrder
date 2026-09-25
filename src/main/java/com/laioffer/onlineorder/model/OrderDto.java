package com.laioffer.onlineorder.model;

import java.time.Instant;
import java.util.List;

// 返回给前端的历史订单，包含支付方式的非敏感展示信息和订单商品快照。
public record OrderDto(
        Long id,
        Double totalPrice,
        String status,
        Instant createdAt,
        String paymentBrand,
        String paymentLastFour,
        List<OrderLineItemDto> lineItems
) {
}
