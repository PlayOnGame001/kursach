package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.station.entity.ServiceStation;

public interface ServiceStationRepository extends JpaRepository<ServiceStation, Long> {
}
