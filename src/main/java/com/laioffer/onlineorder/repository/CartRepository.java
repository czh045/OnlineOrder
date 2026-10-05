package com.laioffer.onlineorder.repository;

import com.laioffer.onlineorder.entity.CartEntity;

import org.springframework.data.jdbc.repository.query.Modifying;

import org.springframework.data.jdbc.repository.query.Query;

import org.springframework.data.repository.ListCrudRepository;

import org.springframework.data.repository.query.Param;

public interface CartRepository extends ListCrudRepository<CartEntity, Long> {

    // SELECT * FROM carts WHERE customer_id = ?
    CartEntity getByCustomerId(Long customerId);

    @Modifying

    @Query("UPDATE carts SET total_price = :totalPrice WHERE id = :cartId")

    void updateTotalPrice(
            @Param("cartId") Long cartId,

            @Param("totalPrice") Double totalPrice
    );
}
