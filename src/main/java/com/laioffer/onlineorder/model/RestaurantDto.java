package com.laioffer.onlineorder.model;

import com.laioffer.onlineorder.entity.RestaurantEntity;

import java.util.List;

public record RestaurantDto(
        Long id,

        String name,

        String address,

        String phone,

        String imageUrl,

        List<MenuItemDto> menuItems
) {
    public RestaurantDto(RestaurantEntity restaurantEntity, List<MenuItemDto> menuItems) {
        this(
                restaurantEntity.id(),

                restaurantEntity.name(),

                restaurantEntity.address(),

                restaurantEntity.phone(),

                restaurantEntity.imageUrl(),

                menuItems
        );
    }
}
