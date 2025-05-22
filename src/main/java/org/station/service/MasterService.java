package org.station.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.station.entity.Master;
import org.station.repository.MasterRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MasterService {
    private final MasterRepository masterRepository;

    public void addMaster(Master master) {
        masterRepository.save(master);
    }

    public boolean removeMaster(long ID) {
        if (masterRepository.findById(ID).isPresent()) {
            masterRepository.deleteById(ID);
            return true;
        }
        return false;
    }

    public List<Master> getAllMasters() {
        try {
            return masterRepository.findAllMastersWithoutJoin();
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    public Optional<Master> getMasterById(long id) {
        return masterRepository.findById(id);
    }
}