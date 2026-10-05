package com.laioffer.onlineorder.service;

import com.laioffer.onlineorder.entity.MenuItemEntity;
import com.laioffer.onlineorder.exception.ResourceNotFoundException;
import com.laioffer.onlineorder.model.MenuItemRequestBody;

import com.laioffer.onlineorder.repository.MenuItemRepository;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;

import java.util.List;

@Service
public class MenuItemService {

    private final MenuItemRepository menuItemRepository;

    public MenuItemService(MenuItemRepository menuItemRepository) {
        this.menuItemRepository = menuItemRepository;
    }

    public List<MenuItemEntity> getMenuItemsByRestaurantId(long restaurantId) {
        return menuItemRepository.getByRestaurantId(restaurantId);
    }

    public MenuItemEntity getMenuItemById(long id) {
        return menuItemRepository.findById(id).get();
    }

    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public MenuItemEntity createMenuItem(long restaurantId, MenuItemRequestBody body) {
        validateMenuItem(body);
        return menuItemRepository.save(new MenuItemEntity(
                null, restaurantId, body.name().trim(), body.description(), body.price(), body.imageUrl()));
    }

    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public MenuItemEntity updateMenuItem(long menuItemId, MenuItemRequestBody body) {
        validateMenuItem(body);
        MenuItemEntity existing = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item " + menuItemId + " not found"));
        return menuItemRepository.save(new MenuItemEntity(
                existing.id(), existing.restaurantId(), body.name().trim(),
                body.description(), body.price(), body.imageUrl()));
    }

    @CacheEvict(cacheNames = "restaurants", allEntries = true)
    public void deleteMenuItem(long menuItemId) {
        if (!menuItemRepository.existsById(menuItemId)) {
            throw new ResourceNotFoundException("Menu item " + menuItemId + " not found");
        }
        menuItemRepository.deleteById(menuItemId);
    }

    private void validateMenuItem(MenuItemRequestBody body) {
        if (body == null || body.name() == null || body.name().isBlank()
                || body.price() == null || body.price() < 0) {
            throw new IllegalArgumentException("Menu item name and a non-negative price are required");
        }
    }
}
