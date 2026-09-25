package com.laioffer.onlineorder.controller;

import com.laioffer.onlineorder.entity.CustomerEntity;
import com.laioffer.onlineorder.model.AddToCartBody;
import com.laioffer.onlineorder.model.CartDto;
import com.laioffer.onlineorder.model.CheckoutBody;
import com.laioffer.onlineorder.model.OrderDto;
import com.laioffer.onlineorder.model.UpdateCartItemBody;
import com.laioffer.onlineorder.service.CartService;
import com.laioffer.onlineorder.service.CustomerService;
import com.laioffer.onlineorder.service.OrderService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartController {

    private final CartService cartService;
    private final CustomerService customerService;
    private final OrderService orderService;

    public CartController(
            CartService cartService,
            CustomerService customerService,
            OrderService orderService
    ) {
        this.cartService = cartService;
        this.customerService = customerService;
        this.orderService = orderService;
    }

    @GetMapping("/cart")
    public CartDto getCart(@AuthenticationPrincipal User user) {
        return cartService.getCart(getCustomer(user).id());
    }

    @PostMapping("/cart")
    public void addToCart(@AuthenticationPrincipal User user, @RequestBody AddToCartBody body) {
        if (body == null || body.menuId() == null) {
            throw new IllegalArgumentException("menu_id is required");
        }
        int quantity = body.quantity() == null ? 1 : body.quantity();
        cartService.addMenuItemToCart(getCustomer(user).id(), body.menuId(), quantity);
    }

    @PatchMapping("/cart/items/{orderItemId}")
    public CartDto updateCartItem(
            @AuthenticationPrincipal User user,
            @PathVariable long orderItemId,
            @RequestBody UpdateCartItemBody body
    ) {
        if (body == null || body.quantity() == null) {
            throw new IllegalArgumentException("quantity is required");
        }
        CustomerEntity customer = getCustomer(user);
        cartService.updateItemQuantity(customer.id(), orderItemId, body.quantity());
        return cartService.getCart(customer.id());
    }

    @DeleteMapping("/cart/items/{orderItemId}")
    public CartDto removeCartItem(
            @AuthenticationPrincipal User user,
            @PathVariable long orderItemId
    ) {
        CustomerEntity customer = getCustomer(user);
        cartService.removeItem(customer.id(), orderItemId);
        return cartService.getCart(customer.id());
    }

    @PostMapping("/cart/checkout")
    public OrderDto checkout(@AuthenticationPrincipal User user, @RequestBody CheckoutBody body) {
        if (body == null || body.paymentMethodId() == null) {
            throw new IllegalArgumentException("payment_method_id is required");
        }
        return orderService.checkout(getCustomer(user).id(), body.paymentMethodId());
    }

    private CustomerEntity getCustomer(User user) {
        return customerService.getCustomerByEmail(user.getUsername());
    }
}
