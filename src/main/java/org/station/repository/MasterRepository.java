package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.station.entity.Client;
import org.station.entity.Master;

public interface MasterRepository extends JpaRepository<Master, Long> { }
