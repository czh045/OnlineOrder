package com.laioffer.onlineorder.repository;

import com.laioffer.onlineorder.entity.CustomerEntity;

import org.springframework.data.jdbc.repository.query.Modifying;

import org.springframework.data.jdbc.repository.query.Query;

import org.springframework.data.repository.ListCrudRepository;

import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CustomerRepository extends ListCrudRepository<CustomerEntity, Long> {


    // SELECT * FROM customers WHERE first_name = ?
    List<CustomerEntity> findByFirstName(String firstName);

    List<CustomerEntity> findByLastName(String lastName);

    CustomerEntity findByEmail(String email);

    @Modifying

    @Query("""
            UPDATE customers
            SET first_name = :firstName, last_name = :lastName
            WHERE email = :email
            """)

    void updateNameByEmail(
            @Param("email") String email,

            @Param("firstName") String firstName,

            @Param("lastName") String lastName
    );
}
