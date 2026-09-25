package com.laioffer.onlineorder.controller;

import com.laioffer.onlineorder.entity.CustomerEntity;
import com.laioffer.onlineorder.model.AddPaymentMethodBody;
import com.laioffer.onlineorder.model.PaymentMethodDto;
import com.laioffer.onlineorder.service.CustomerService;
import com.laioffer.onlineorder.service.PaymentMethodService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PaymentMethodController {

    private final PaymentMethodService paymentMethodService;
    private final CustomerService customerService;

    public PaymentMethodController(
            PaymentMethodService paymentMethodService,
            CustomerService customerService
    ) {
        this.paymentMethodService = paymentMethodService;
        this.customerService = customerService;
    }

    @GetMapping("/payment-methods")
    public List<PaymentMethodDto> getPaymentMethods(@AuthenticationPrincipal User user) {
        CustomerEntity customer = customerService.getCustomerByEmail(user.getUsername());
        return paymentMethodService.getPaymentMethods(customer.id());
    }

    @PostMapping("/payment-methods")
    @ResponseStatus(HttpStatus.CREATED)
    public PaymentMethodDto addPaymentMethod(
            @AuthenticationPrincipal User user,
            @RequestBody AddPaymentMethodBody body
    ) {
        CustomerEntity customer = customerService.getCustomerByEmail(user.getUsername());
        return paymentMethodService.addPaymentMethod(customer.id(), body);
    }
}
