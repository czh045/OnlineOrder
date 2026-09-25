package com.laioffer.onlineorder.model;

public record RestaurantRequestBody(
        String name,
        String address,
        String phone,
        String imageUrl
) {
}
