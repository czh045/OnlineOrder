package com.laioffer.onlineorder.repository;

import com.laioffer.onlineorder.entity.MenuItemEntity;

import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface MenuItemRepository extends ListCrudRepository<MenuItemEntity, Long> {

    // SELECT * FROM menu_items WHERE restaurant_id = ?
    List<MenuItemEntity> getByRestaurantId(Long restaurantId);
}
