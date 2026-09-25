// service 包放业务层代码。
package com.laioffer.onlineorder.service;

// MenuItemEntity 对应 menu_items 表。
import com.laioffer.onlineorder.entity.MenuItemEntity;
import com.laioffer.onlineorder.exception.ResourceNotFoundException;
import com.laioffer.onlineorder.model.MenuItemRequestBody;

// MenuItemRepository 负责访问 menu_items 表。
import com.laioffer.onlineorder.repository.MenuItemRepository;

// Service 注解表示这是一个业务服务类。
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;

// List 表示返回多个菜单项。
import java.util.List;

// @Service 让 Spring 管理这个类。
@Service
public class MenuItemService {

    // menuItemRepository 用来执行菜单相关数据库查询。
    private final MenuItemRepository menuItemRepository;

    // 构造函数注入。
    // Spring 会自动把 MenuItemRepository 的实现传进来。
    public MenuItemService(MenuItemRepository menuItemRepository) {
        // 保存 Repository，给下面的方法使用。
        this.menuItemRepository = menuItemRepository;
    }

    // 根据餐厅 ID 查询菜单。
    // Controller 的 GET /restaurant/{restaurantId}/menu 会调用这个方法。
    public List<MenuItemEntity> getMenuItemsByRestaurantId(long restaurantId) {
        // 直接调用 Repository 的方法查数据库。
        // Spring Data JDBC 会根据 getByRestaurantId 自动生成 SQL。
        return menuItemRepository.getByRestaurantId(restaurantId);
    }

    // 根据菜单 ID 查询单个菜品。
    // 当前项目里这个方法备用，购物车加购主要直接在 CartService 查菜单。
    public MenuItemEntity getMenuItemById(long id) {
        // findById 是 ListCrudRepository 自带的方法。
        // 它返回 Optional，所以这里用 .get() 取出里面的 MenuItemEntity。
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
