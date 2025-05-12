package org.station.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.station.entity.Repair;
import org.station.repository.RepairRepository;

import java.time.LocalDateTime;
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

    @Transactional
    public void acceptRepair(Long repairId) {
        Repair repair = repairRepository.findById(repairId).orElseThrow();
        repair.setStatus("ACCEPTED");
        repair.setStartedAt(LocalDateTime.now());
        repairRepository.save(repair);
    }

    @Transactional
    public void rejectRepair(Long repairId) {
        Repair repair = repairRepository.findById(repairId).orElseThrow();
        repair.setStatus("REJECTED");
        repairRepository.save(repair);
    }

    public void acceptRepair(Long id, LocalDateTime startTime) {
        Repair repair = repairRepository.findById(id).orElseThrow();
        repair.setStartDateTime(startTime);
        repair.setStatus("IN_PROGRESS");
        repairRepository.save(repair);
    }

    public void completeRepair(Long id, LocalDateTime endTime) {
        Repair repair = repairRepository.findById(id).orElseThrow();
        repair.setEndDateTime(endTime);
        repair.setStatus("COMPLETED");
        repairRepository.save(repair);
    }

    public List<Repair> getAllRepairsWithSpares() {
        return repairRepository.findAllWithRepairTypeAndSpares();
    }

}
