package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.station.entity.RepairType;

public interface RepairTypeRepository extends JpaRepository<RepairType, Long> {
}
