package com.spbutu.gia.core.domain.repository.deanery;

import com.spbutu.gia.core.domain.entity.deanery.DeaneryOrder;
import com.spbutu.gia.core.domain.enums.OrderStatus;
import com.spbutu.gia.core.domain.enums.OrderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeaneryOrderRepository extends JpaRepository<DeaneryOrder, UUID> {

    List<DeaneryOrder> findAllByType(OrderType type);

    List<DeaneryOrder> findAllByStatus(OrderStatus status);

    List<DeaneryOrder> findAllByOrderByOrderDateDesc();
}
