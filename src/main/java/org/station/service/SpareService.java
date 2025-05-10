package org.station.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.station.entity.Spare;
import org.station.repository.SpareRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpareService {
    private final SpareRepository spareRepository;

    public Spare addPart(Spare spare) {
        return spareRepository.save(spare);
    }

    public boolean removePart(Long id) {
        spareRepository.deleteById(id);
        return true;
    }

    public List<Spare> getAllParts() {
        return spareRepository.findAll();
    }

    public Optional<Spare> getPartById(Long id) {
        return spareRepository.findById(id);
    }

    public Optional<Spare> getPartByName(String name) {
        return spareRepository.findAll().stream()
                .filter(spare -> spare.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public void updatePart(Spare spare) {
        spareRepository.save(spare);
    }
}
