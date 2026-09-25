package com.laioffer.onlineorder.repository;

import com.laioffer.onlineorder.entity.PaymentMethodEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface PaymentMethodRepository extends ListCrudRepository<PaymentMethodEntity, Long> {

    List<PaymentMethodEntity> findByCustomerId(Long customerId);
}
