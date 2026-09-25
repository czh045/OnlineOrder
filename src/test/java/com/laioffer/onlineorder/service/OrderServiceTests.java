package com.laioffer.onlineorder.service;

import com.laioffer.onlineorder.entity.OrderEntity;
import com.laioffer.onlineorder.entity.OrderLineItemEntity;
import com.laioffer.onlineorder.entity.PaymentMethodEntity;
import com.laioffer.onlineorder.exception.ResourceNotFoundException;
import com.laioffer.onlineorder.model.CartDto;
import com.laioffer.onlineorder.model.OrderDto;
import com.laioffer.onlineorder.model.OrderItemDto;
import com.laioffer.onlineorder.repository.OrderLineItemRepository;
import com.laioffer.onlineorder.repository.OrderRepository;
import com.laioffer.onlineorder.repository.PaymentMethodRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class OrderServiceTests {

    private CartService cartService;
    private OrderRepository orderRepository;
    private OrderLineItemRepository orderLineItemRepository;
    private PaymentMethodRepository paymentMethodRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        cartService = mock(CartService.class);
        orderRepository = mock(OrderRepository.class);
        orderLineItemRepository = mock(OrderLineItemRepository.class);
        paymentMethodRepository = mock(PaymentMethodRepository.class);
        orderService = new OrderService(
                cartService,
                orderRepository,
                orderLineItemRepository,
                paymentMethodRepository
        );
    }

    @Test
    void checkout_ownedPaymentMethod_createsOrderSnapshotAndClearsCart() {
        PaymentMethodEntity paymentMethod = new PaymentMethodEntity(
                30L, 20L, "Alex Chen", "Visa", "4242", 12, 2030
        );
        CartDto cart = new CartDto(
                10L,
                20L,
                25.0,
                List.of(new OrderItemDto(
                        40L,
                        5L,
                        2L,
                        12.5,
                        2,
                        "Chicken Bowl",
                        "Grilled chicken and rice",
                        "https://example.com/chicken.jpg"
                ))
        );
        OrderEntity savedOrder = new OrderEntity(
                50L, 20L, 30L, 25.0, "PAID", Instant.parse("2026-09-25T12:00:00Z")
        );

        when(paymentMethodRepository.findById(30L)).thenReturn(Optional.of(paymentMethod));
        when(cartService.getCart(20L)).thenReturn(cart);
        when(orderRepository.save(any(OrderEntity.class))).thenReturn(savedOrder);

        OrderDto result = orderService.checkout(20L, 30L);

        ArgumentCaptor<OrderEntity> orderCaptor = ArgumentCaptor.forClass(OrderEntity.class);
        verify(orderRepository).save(orderCaptor.capture());
        assertEquals(20L, orderCaptor.getValue().customerId());
        assertEquals(30L, orderCaptor.getValue().paymentMethodId());
        assertEquals(25.0, orderCaptor.getValue().totalPrice());
        assertEquals("PAID", orderCaptor.getValue().status());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<Iterable<OrderLineItemEntity>> lineItemsCaptor =
                ArgumentCaptor.forClass(Iterable.class);
        verify(orderLineItemRepository).saveAll(lineItemsCaptor.capture());
        OrderLineItemEntity savedLineItem = lineItemsCaptor.getValue().iterator().next();
        assertEquals(50L, savedLineItem.orderId());
        assertEquals(5L, savedLineItem.menuItemId());
        assertEquals("Chicken Bowl", savedLineItem.menuItemName());
        assertEquals(12.5, savedLineItem.price());
        assertEquals(2, savedLineItem.quantity());

        verify(cartService).clearCart(20L);
        assertEquals(50L, result.id());
        assertEquals("Visa", result.paymentBrand());
        assertEquals("4242", result.paymentLastFour());
        assertEquals(1, result.lineItems().size());
    }

    @Test
    void checkout_paymentMethodOwnedByAnotherCustomer_isRejected() {
        PaymentMethodEntity anotherUsersCard = new PaymentMethodEntity(
                30L, 99L, "Another User", "Visa", "1111", 12, 2030
        );
        when(paymentMethodRepository.findById(30L)).thenReturn(Optional.of(anotherUsersCard));

        assertThrows(ResourceNotFoundException.class, () -> orderService.checkout(20L, 30L));

        verify(cartService, never()).getCart(any(Long.class));
        verify(orderRepository, never()).save(any(OrderEntity.class));
        verify(orderLineItemRepository, never()).saveAll(any());
    }
}
