// service 包放业务层代码。
package com.laioffer.onlineorder.service;

// MenuItemEntity 对应 menu_items 表。
import com.laioffer.onlineorder.entity.MenuItemEntity;

// RestaurantEntity 对应 restaurants 表。
import com.laioffer.onlineorder.entity.RestaurantEntity;
import com.laioffer.onlineorder.exception.ResourceNotFoundException;

// MenuItemDto 是返回给前端的菜单数据结构。
import com.laioffer.onlineorder.model.MenuItemDto;

// RestaurantDto 是返回给前端的餐厅数据结构，里面包含菜单列表。
import com.laioffer.onlineorder.model.RestaurantDto;
import com.laioffer.onlineorder.model.RestaurantRequestBody;

// MenuItemRepository 用来查询菜单数据。
import com.laioffer.onlineorder.repository.MenuItemRepository;

// RestaurantRepository 用来查询餐厅数据。
import com.laioffer.onlineorder.repository.RestaurantRepository;

// Cacheable 表示方法结果可以缓存。
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;

// Service 表示这是业务层组件。
import org.springframework.stereotype.Service;

// ArrayList 是可变列表。
import java.util.ArrayList;

// HashMap 是 key-value 映射。
import java.util.HashMap;

// List 是列表接口。
import java.util.List;

// Map 是映射接口。
import java.util.Map;

// @Service 让 Spring 管理这个业务类。
@Service
public class RestaurantService {

    // menuItemRepository 负责查询菜单表。
    private final MenuItemRepository menuItemRepository;

    // restaurantRepository 负责查询餐厅表。
    private final RestaurantRepository restaurantRepository;

    // 构造函数注入两个 Repository。
    public RestaurantService(
            // 餐厅数据访问对象。
            RestaurantRepository restaurantRepository,
            // 菜单数据访问对象。
            MenuItemRepository menuItemRepository
    ) {
        // 保存 RestaurantRepository。
        this.restaurantRepository = restaurantRepository;

        // 保存 MenuItemRepository。
        this.menuItemRepository = menuItemRepository;
    }

    // @Cacheable("restaurants") 表示这个方法结果会放进 restaurants 缓存。
    // 餐厅和菜单数据通常不频繁变化，缓存后可以减少数据库查询。
    @Cacheable("restaurants")

    // getRestaurants 返回“所有餐厅 + 每家餐厅的菜单”。
    public List<RestaurantDto> getRestaurants() {
        // 查询 restaurants 表，拿到所有餐厅。
        List<RestaurantEntity> restaurantEntities = restaurantRepository.findAll();

        // 查询 menu_items 表，拿到所有菜单。
        List<MenuItemEntity> menuItemEntities = menuItemRepository.findAll();

        // 创建一个 Map，用来按 restaurantId 给菜单分组。
        // key 是餐厅 ID，value 是这家餐厅的菜单列表。
        Map<Long, List<MenuItemDto>> groupedMenuItems = new HashMap<>();

        // 遍历每一道菜。
        for (MenuItemEntity menuItemEntity : menuItemEntities) {
            // computeIfAbsent 的意思是：
            // 如果 groupedMenuItems 里已经有这个 restaurantId，就直接取出对应列表；
            // 如果还没有，就创建一个新的 ArrayList 放进去，然后返回这个新列表。
            List<MenuItemDto> group = groupedMenuItems.computeIfAbsent(
                    // 用这道菜的 restaurantId 作为分组 key。
                    menuItemEntity.restaurantId(),
                    // key -> new ArrayList<>() 是 lambda 写法。
                    // 当 Map 里还没有这个 key 时，就创建一个空列表。
                    key -> new ArrayList<>()
            );

            // 把当前菜单实体转换成 MenuItemDto，再放进对应餐厅的菜单列表里。
            group.add(new MenuItemDto(menuItemEntity));
        }

        // 创建最终返回给前端的餐厅列表。
        List<RestaurantDto> results = new ArrayList<>();

        // 遍历每一家餐厅。
        for (RestaurantEntity restaurantEntity : restaurantEntities) {
            // 创建 RestaurantDto。
            // 第一个参数是餐厅实体。
            // 第二个参数是该餐厅对应的菜单列表。
            RestaurantDto restaurantDto = new RestaurantDto(
                    restaurantEntity,

                    // getOrDefault 的意思是：
                    // 如果 groupedMenuItems 里有这家餐厅的菜单，就返回菜单列表；
                    // 如果没有，就返回空列表，避免 null。
                    groupedMenuItems.getOrDefault(restaurantEntity.id(), List.of())
            );

            // 把组装好的餐厅 DTO 加入结果列表。
            results.add(restaurantDto);
        }

        // 返回“餐厅 + 菜单”的完整列表。
        return results;
    }

    // 管理端写入后清空列表缓存，下一次查询才能看到最新菜单。
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
