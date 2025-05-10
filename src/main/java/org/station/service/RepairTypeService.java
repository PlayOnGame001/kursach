package org.station.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.station.entity.RepairType;
import org.station.repository.RepairTypeRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepairTypeService {
    private final RepairTypeRepository repairTypeRepository;

    public RepairType addRepairType(RepairType repairType) {
        return repairTypeRepository.save(repairType);
    }

    public boolean removeRepairType(Long id) {
        repairTypeRepository.deleteById(id);
        return true;
    }

    public List<RepairType> getAllRepairTypes() {
        return repairTypeRepository.findAll();
    }

    public Optional<RepairType> getRepairTypeById(Long id) {
        return repairTypeRepository.findById(id);
    }
}
