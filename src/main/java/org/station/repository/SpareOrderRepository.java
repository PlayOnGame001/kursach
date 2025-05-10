package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.station.entity.SpareOrder;

import java.util.List;

public interface SpareOrderRepository extends JpaRepository<SpareOrder, Long> {
    List<SpareOrder> findByRepairId(Long repairId);

}
