package com.laioffer.onlineorder.service;

import com.laioffer.onlineorder.entity.CartEntity;

import com.laioffer.onlineorder.entity.MenuItemEntity;

import com.laioffer.onlineorder.entity.OrderItemEntity;

import com.laioffer.onlineorder.model.CartDto;

import com.laioffer.onlineorder.repository.CartRepository;

import com.laioffer.onlineorder.repository.MenuItemRepository;

import com.laioffer.onlineorder.repository.OrderItemRepository;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.util.List;

import java.util.Optional;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.verify;

import static org.mockito.Mockito.when;

class CartServiceTests {

    private CartRepository cartRepository;

    private MenuItemRepository menuItemRepository;

    private OrderItemRepository orderItemRepository;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartRepository = mock(CartRepository.class);

        menuItemRepository = mock(MenuItemRepository.class);

        orderItemRepository = mock(OrderItemRepository.class);

        cartService = new CartService(cartRepository, menuItemRepository, orderItemRepository);
    }

    @Test
    void addMenuItemToCart_newItem_createsOrderItemAndUpdatesTotalPrice() {
        CartEntity cart = new CartEntity(10L, 20L, 5.0);

        MenuItemEntity menuItem = new MenuItemEntity(1L, 2L, "beef", "beef description", 10.0, "image.jpg");

        when(cartRepository.getByCustomerId(20L)).thenReturn(cart);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));

        when(orderItemRepository.findByCartIdAndMenuItemId(10L, 1L)).thenReturn(null);

        when(orderItemRepository.sumPriceByCartId(10L)).thenReturn(15.0);

        cartService.addMenuItemToCart(20L, 1L);

        verify(orderItemRepository).save(new OrderItemEntity(null, 1L, 10L, 10.0, 1));

        verify(cartRepository).updateTotalPrice(10L, 15.0);
    }

    @Test
    void addMenuItemToCart_existingItem_increasesQuantityAndUpdatesTotalPrice() {
        CartEntity cart = new CartEntity(10L, 20L, 5.0);

        MenuItemEntity menuItem = new MenuItemEntity(1L, 2L, "beef", "beef description", 10.0, "image.jpg");

        // id=30，menuItemId=1，cartId=10，price=10.0，quantity=2。
        OrderItemEntity orderItem = new OrderItemEntity(30L, 1L, 10L, 10.0, 2);

        when(cartRepository.getByCustomerId(20L)).thenReturn(cart);

        when(menuItemRepository.findById(1L)).thenReturn(Optional.of(menuItem));

        when(orderItemRepository.findByCartIdAndMenuItemId(10L, 1L)).thenReturn(orderItem);

        when(orderItemRepository.sumPriceByCartId(10L)).thenReturn(15.0);

        cartService.addMenuItemToCart(20L, 1L);

        verify(orderItemRepository).save(new OrderItemEntity(30L, 1L, 10L, 10.0, 3));

        verify(cartRepository).updateTotalPrice(10L, 15.0);
    }

    @Test
    void getCart_oneOrderItem_returnsCartDto() {
        CartEntity cart = new CartEntity(10L, 20L, 10.0);

        OrderItemEntity orderItem = new OrderItemEntity(30L, 1L, 10L, 10.0, 1);

        MenuItemEntity menuItem = new MenuItemEntity(1L, 2L, "beef", "beef description", 10.0, "image.jpg");

        when(cartRepository.getByCustomerId(20L)).thenReturn(cart);

        when(orderItemRepository.getAllByCartId(10L)).thenReturn(List.of(orderItem));

        when(menuItemRepository.findAllById(Set.of(1L))).thenReturn(List.of(menuItem));

        CartDto cartDto = cartService.getCart(20L);

        assertEquals(10L, cartDto.id());

        assertEquals(20L, cartDto.customerId());

        assertEquals(10.0, cartDto.totalPrice());

        assertEquals(1, cartDto.orderItems().size());

        assertEquals(30L, cartDto.orderItems().get(0).id());

        assertEquals(1L, cartDto.orderItems().get(0).menuItemId());

        assertEquals("beef", cartDto.orderItems().get(0).menuItemName());
    }

    @Test
    void clearCart_deletesOrderItemsAndResetsTotalPrice() {
        CartEntity cart = new CartEntity(10L, 20L, 10.0);

        when(cartRepository.getByCustomerId(20L)).thenReturn(cart);

        cartService.clearCart(20L);

        verify(orderItemRepository).deleteByCartId(10L);

        verify(cartRepository).updateTotalPrice(10L, 0.0);
    }
}
