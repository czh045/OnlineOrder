package com.laioffer.onlineorder.controller;

import com.laioffer.onlineorder.entity.MenuItemEntity;
import com.laioffer.onlineorder.entity.RestaurantEntity;
import com.laioffer.onlineorder.model.MenuItemRequestBody;
import com.laioffer.onlineorder.model.RestaurantDto;
import com.laioffer.onlineorder.model.RestaurantRequestBody;
import com.laioffer.onlineorder.service.MenuItemService;
import com.laioffer.onlineorder.service.RestaurantService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MenuController {

    private final RestaurantService restaurantService;
    private final MenuItemService menuItemService;

    public MenuController(RestaurantService restaurantService, MenuItemService menuItemService) {
        this.restaurantService = restaurantService;
        this.menuItemService = menuItemService;
    }

    @GetMapping("/restaurant/{restaurantId}/menu")
    public List<MenuItemEntity> getMenuByRestaurant(@PathVariable long restaurantId) {
        return menuItemService.getMenuItemsByRestaurantId(restaurantId);
    }

    @GetMapping("/restaurants/menu")
    public List<RestaurantDto> getMenuForAllRestaurants() {
        return restaurantService.getRestaurants();
    }

    // 下列写接口会被 AppConfig 中的 hasRole("ADMIN") 规则保护。
    @PostMapping("/restaurants")
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantEntity createRestaurant(@RequestBody RestaurantRequestBody body) {
        return restaurantService.createRestaurant(body);
    }

    @PutMapping("/restaurant/{restaurantId}")
    public RestaurantEntity updateRestaurant(
            @PathVariable long restaurantId,
            @RequestBody RestaurantRequestBody body
    ) {
        return restaurantService.updateRestaurant(restaurantId, body);
    }

    @DeleteMapping("/restaurant/{restaurantId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRestaurant(@PathVariable long restaurantId) {
        restaurantService.deleteRestaurant(restaurantId);
    }

    @PostMapping("/restaurant/{restaurantId}/menu")
    @ResponseStatus(HttpStatus.CREATED)
    public MenuItemEntity createMenuItem(
            @PathVariable long restaurantId,
            @RequestBody MenuItemRequestBody body
    ) {
        return menuItemService.createMenuItem(restaurantId, body);
    }

    @PutMapping("/menu/{menuItemId}")
    public MenuItemEntity updateMenuItem(
            @PathVariable long menuItemId,
            @RequestBody MenuItemRequestBody body
    ) {
        return menuItemService.updateMenuItem(menuItemId, body);
    }

    @DeleteMapping("/menu/{menuItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable long menuItemId) {
        menuItemService.deleteMenuItem(menuItemId);
    }
}
