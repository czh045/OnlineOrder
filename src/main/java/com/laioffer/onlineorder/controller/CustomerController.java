package com.laioffer.onlineorder.controller;

import com.laioffer.onlineorder.model.RegisterBody;
import com.laioffer.onlineorder.model.CurrentUserDto;

import com.laioffer.onlineorder.service.CustomerService;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;

import org.springframework.web.bind.annotation.ResponseStatus;

import org.springframework.web.bind.annotation.RestController;

@RestController

public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // POST /signup：
    @PostMapping("/signup")

    @ResponseStatus(value = HttpStatus.CREATED)

    // @RequestBody RegisterBody body：
    public void signUp(@RequestBody RegisterBody body) {
        customerService.signUp(body.email(), body.password(), body.firstName(), body.lastName());
    }

    @GetMapping("/me")
    public CurrentUserDto getCurrentUser(Authentication authentication) {
        boolean isAdmin = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        return new CurrentUserDto(authentication.getName(), isAdmin);
    }
}
