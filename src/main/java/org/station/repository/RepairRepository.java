package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.station.entity.Repair;

public interface RepairRepository extends JpaRepository<Repair, Long> {
}
