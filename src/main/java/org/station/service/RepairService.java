package org.station.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.station.entity.Repair;
import org.station.repository.RepairRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RepairService {
    private final RepairRepository repairRepository;

    public Repair addRepair(Repair repair) {
        return repairRepository.save(repair);
    }

    public boolean removeRepair(Long id) {
        repairRepository.deleteById(id);
        return true;
    }

    public List<Repair> getAllRepairs() {
        return repairRepository.findAll();
    }

    public Optional<Repair> getRepairById(Long id) {
        return repairRepository.findById(id);
    }
}
