package org.station.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.station.entity.ServiceStation;
import org.station.repository.ServiceStationRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServiceStationService {

    private final ServiceStationRepository stationRepository;

    public List<ServiceStation> getAllStations() {
        return stationRepository.findAll();
    }

    public Optional<ServiceStation> getById(Long id) {
        return stationRepository.findById(id);
    }

    public ServiceStation addStation(ServiceStation station) {
        return stationRepository.save(station);
    }

    public boolean deleteStation(Long id) {
        if (stationRepository.existsById(id)) {
            stationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
