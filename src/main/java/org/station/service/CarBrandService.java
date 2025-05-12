package org.station.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.station.entity.CarBrand;
import org.station.repository.CarBrandRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarBrandService {
    private final CarBrandRepository brandRepository;

    public List<CarBrand> getAllBrands() {
        return brandRepository.findAll();
    }
}
