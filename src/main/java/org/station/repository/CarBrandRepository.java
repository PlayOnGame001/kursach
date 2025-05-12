package org.station.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.station.entity.CarBrand;

import java.util.Optional;

public interface CarBrandRepository extends JpaRepository<CarBrand, Long> {
    Optional<CarBrand> findByNameIgnoreCase(String name);
}
