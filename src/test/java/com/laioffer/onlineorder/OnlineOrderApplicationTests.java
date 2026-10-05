package com.laioffer.onlineorder;

import com.laioffer.onlineorder.repository.CartRepository;
import com.laioffer.onlineorder.repository.CustomerRepository;
import com.laioffer.onlineorder.repository.MenuItemRepository;
import com.laioffer.onlineorder.repository.OrderItemRepository;
import com.laioffer.onlineorder.repository.OrderLineItemRepository;
import com.laioffer.onlineorder.repository.OrderRepository;
import com.laioffer.onlineorder.repository.PaymentMethodRepository;
import com.laioffer.onlineorder.repository.RestaurantRepository;

import org.junit.jupiter.api.Test;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(properties = {
        "spring.sql.init.mode=never",

        "onlineorder.dev-runner.enabled=false",

        "spring.autoconfigure.exclude=org.springframework.boot.data.jdbc.autoconfigure.DataJdbcRepositoriesAutoConfiguration"
})
class OnlineOrderApplicationTests {

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private CustomerRepository customerRepository;

    @MockitoBean
    private MenuItemRepository menuItemRepository;

    @MockitoBean
    private OrderItemRepository orderItemRepository;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private OrderLineItemRepository orderLineItemRepository;

    @MockitoBean
    private PaymentMethodRepository paymentMethodRepository;

    @MockitoBean
    private RestaurantRepository restaurantRepository;

    @Test
    void contextLoads() {
    }

}
