// PartRepository.java
package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.station.entity.Spare;

import java.util.Optional;

public interface SpareRepository extends JpaRepository<Spare, Long> {
    Optional<Spare> findByName(String name);

}
