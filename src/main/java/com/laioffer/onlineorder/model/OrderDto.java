package com.laioffer.onlineorder.model;

import java.time.Instant;
import java.util.List;

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
