package com.laioffer.onlineorder.service;

import com.laioffer.onlineorder.entity.MenuItemEntity;

import com.laioffer.onlineorder.entity.RestaurantEntity;
import com.laioffer.onlineorder.exception.ResourceNotFoundException;

import com.laioffer.onlineorder.model.MenuItemDto;

import com.laioffer.onlineorder.model.RestaurantDto;
import com.laioffer.onlineorder.model.RestaurantRequestBody;

import com.laioffer.onlineorder.repository.MenuItemRepository;

import com.laioffer.onlineorder.repository.RestaurantRepository;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

import org.springframework.stereotype.Service;

import java.util.ArrayList;

import java.util.HashMap;

import java.util.List;

import java.util.Map;

@Service
public class RestaurantService {

    private final MenuItemRepository menuItemRepository;

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(
            RestaurantRepository restaurantRepository,
            MenuItemRepository menuItemRepository
    ) {
        this.restaurantRepository = restaurantRepository;

        this.menuItemRepository = menuItemRepository;
    }

    @Cacheable("restaurants")

    public List<RestaurantDto> getRestaurants() {
        List<RestaurantEntity> restaurantEntities = restaurantRepository.findAll();

        List<MenuItemEntity> menuItemEntities = menuItemRepository.findAll();

        Map<Long, List<MenuItemDto>> groupedMenuItems = new HashMap<>();

        for (MenuItemEntity menuItemEntity : menuItemEntities) {
            List<MenuItemDto> group = groupedMenuItems.computeIfAbsent(
                    menuItemEntity.restaurantId(),
                    key -> new ArrayList<>()
            );

            group.add(new MenuItemDto(menuItemEntity));
        }

        List<RestaurantDto> results = new ArrayList<>();

        for (RestaurantEntity restaurantEntity : restaurantEntities) {
            RestaurantDto restaurantDto = new RestaurantDto(
                    restaurantEntity,

                    groupedMenuItems.getOrDefault(restaurantEntity.id(), List.of())
            );

            results.add(restaurantDto);
        }

        return results;
    }

    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public RestaurantEntity createRestaurant(RestaurantRequestBody body) {
        validateRestaurant(body);
        return restaurantRepository.save(new RestaurantEntity(
                null, body.name().trim(), body.address().trim(), body.phone(), body.imageUrl()));
    }

    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public RestaurantEntity updateRestaurant(long restaurantId, RestaurantRequestBody body) {
        validateRestaurant(body);
        RestaurantEntity existing = restaurantRepository.findById(restaurantId)
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant " + restaurantId + " not found"));
        return restaurantRepository.save(new RestaurantEntity(
                existing.id(), body.name().trim(), body.address().trim(), body.phone(), body.imageUrl()));
    }

    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public void deleteRestaurant(long restaurantId) {
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant " + restaurantId + " not found");
        }
        restaurantRepository.deleteById(restaurantId);
    }

    private void validateRestaurant(RestaurantRequestBody body) {
        if (body == null || body.name() == null || body.name().isBlank()
                || body.address() == null || body.address().isBlank()) {
            throw new IllegalArgumentException("Restaurant name and address are required");
        }
    }
}
