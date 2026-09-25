package com.laioffer.onlineorder.repository;

import com.laioffer.onlineorder.entity.OrderLineItemEntity;
import org.springframework.data.repository.ListCrudRepository;

import java.util.List;

public interface OrderLineItemRepository extends ListCrudRepository<OrderLineItemEntity, Long> {

    List<OrderLineItemEntity> findByOrderId(Long orderId);
}
