package com.laioffer.onlineorder.model;

public record MenuItemRequestBody(
        String name,
        String description,
        Double price,
        String imageUrl
) {
}
