package com.laioffer.onlineorder.service;

import com.laioffer.onlineorder.entity.CartEntity;
import com.laioffer.onlineorder.entity.MenuItemEntity;
import com.laioffer.onlineorder.entity.OrderItemEntity;
import com.laioffer.onlineorder.exception.ResourceNotFoundException;
import com.laioffer.onlineorder.model.CartDto;
import com.laioffer.onlineorder.model.OrderItemDto;
import com.laioffer.onlineorder.repository.CartRepository;
import com.laioffer.onlineorder.repository.MenuItemRepository;
import com.laioffer.onlineorder.repository.OrderItemRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final MenuItemRepository menuItemRepository;
    private final OrderItemRepository orderItemRepository;

    public CartService(
            CartRepository cartRepository,
            MenuItemRepository menuItemRepository,
            OrderItemRepository orderItemRepository
    ) {
        this.cartRepository = cartRepository;
        this.menuItemRepository = menuItemRepository;
        this.orderItemRepository = orderItemRepository;
    }

    // 保留课程旧接口，旧版 Postman 请求仍可正常“加一件”。
    public void addMenuItemToCart(long customerId, long menuItemId) {
        addMenuItemToCart(customerId, menuItemId, 1);
    }

    @CacheEvict(cacheNames = "cart", key = "#customerId")
    @Transactional
    public void addMenuItemToCart(long customerId, long menuItemId, int quantityToAdd) {
        if (quantityToAdd <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }

        CartEntity cart = getRequiredCart(customerId);
        MenuItemEntity menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Menu item " + menuItemId + " not found"));

        OrderItemEntity existingItem = orderItemRepository.findByCartIdAndMenuItemId(cart.id(), menuItemId);
        OrderItemEntity itemToSave = existingItem == null
                ? new OrderItemEntity(null, menuItem.id(), cart.id(), menuItem.price(), quantityToAdd)
                : new OrderItemEntity(
                        existingItem.id(),
                        existingItem.menuItemId(),
                        existingItem.cartId(),
                        existingItem.price(),
                        existingItem.quantity() + quantityToAdd
                );

        orderItemRepository.save(itemToSave);
        refreshCartTotal(cart.id());
    }

    @Cacheable(cacheNames = "cart", key = "#customerId")
    public CartDto getCart(long customerId) {
        CartEntity cart = getRequiredCart(customerId);
        List<OrderItemEntity> orderItems = orderItemRepository.getAllByCartId(cart.id());

        Set<Long> menuItemIds = orderItems.stream()
                .map(OrderItemEntity::menuItemId)
                .collect(Collectors.toSet());
        Map<Long, MenuItemEntity> menuItemsById = menuItemRepository.findAllById(menuItemIds).stream()
                .collect(Collectors.toMap(MenuItemEntity::id, Function.identity()));

        List<OrderItemDto> itemDtos = orderItems.stream()
                .map(orderItem -> {
                    MenuItemEntity menuItem = menuItemsById.get(orderItem.menuItemId());
                    if (menuItem == null) {
                        throw new ResourceNotFoundException(
                                "Menu item " + orderItem.menuItemId() + " not found for cart item " + orderItem.id());
                    }
                    return new OrderItemDto(orderItem, menuItem);
                })
                .toList();

        return new CartDto(cart, itemDtos);
    }

    @CacheEvict(cacheNames = "cart", key = "#customerId")
    @Transactional
    public void updateItemQuantity(long customerId, long orderItemId, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        CartEntity cart = getRequiredCart(customerId);
        OrderItemEntity orderItem = getOwnedOrderItem(cart, orderItemId);

        if (quantity == 0) {
            orderItemRepository.deleteById(orderItem.id());
        } else {
            orderItemRepository.save(new OrderItemEntity(
                    orderItem.id(),
                    orderItem.menuItemId(),
                    orderItem.cartId(),
                    orderItem.price(),
                    quantity
            ));
        }
        refreshCartTotal(cart.id());
    }

    @CacheEvict(cacheNames = "cart", key = "#customerId")
    @Transactional
    public void removeItem(long customerId, long orderItemId) {
        CartEntity cart = getRequiredCart(customerId);
        OrderItemEntity orderItem = getOwnedOrderItem(cart, orderItemId);
        orderItemRepository.deleteById(orderItem.id());
        refreshCartTotal(cart.id());
    }

    @CacheEvict(cacheNames = "cart", key = "#customerId")
    @Transactional
    public void clearCart(long customerId) {
        CartEntity cart = getRequiredCart(customerId);
        orderItemRepository.deleteByCartId(cart.id());
        cartRepository.updateTotalPrice(cart.id(), 0.0);
    }

    private CartEntity getRequiredCart(long customerId) {
        CartEntity cart = cartRepository.getByCustomerId(customerId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart for customer " + customerId + " not found");
        }
        return cart;
    }

    // 先从当前用户自己的购物车取 cartId，再核验目标行的归属，避免猜 ID 越权改别人的购物车。
    private OrderItemEntity getOwnedOrderItem(CartEntity cart, long orderItemId) {
        OrderItemEntity orderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item " + orderItemId + " not found"));
        if (!cart.id().equals(orderItem.cartId())) {
            throw new ResourceNotFoundException("Cart item " + orderItemId + " not found");
        }
        return orderItem;
    }

    private void refreshCartTotal(long cartId) {
        cartRepository.updateTotalPrice(cartId, orderItemRepository.sumPriceByCartId(cartId));
    }
}
