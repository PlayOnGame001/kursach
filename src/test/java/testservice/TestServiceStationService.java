package testservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.station.entity.ServiceStation;
import org.station.repository.ServiceStationRepository;
import org.station.service.ServiceStationService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestServiceStationService {
    private ServiceStationRepository repository;
    private ServiceStationService service;

    @BeforeEach
    void setup() {
        repository = mock(ServiceStationRepository.class);
        service = new ServiceStationService(repository);
    }

    @Test
    void testAddStation() {
        ServiceStation station = new ServiceStation();
        service.addStation(station);
        verify(repository).save(station);
    }

    @Test
    void testDeleteStationSuccess() {
        when(repository.existsById(1L)).thenReturn(true);
        assertTrue(service.deleteStation(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void testDeleteStationFail() {
        when(repository.existsById(2L)).thenReturn(false);
        assertFalse(service.deleteStation(2L));
    }

    @Test
    void testGetAllStations() {
        when(repository.findAll()).thenReturn(List.of(new ServiceStation()));
        assertEquals(1, service.getAllStations().size());
    }

    @Test
    void testGetById() {
        ServiceStation station = new ServiceStation();
        when(repository.findById(1L)).thenReturn(Optional.of(station));
        assertEquals(station, service.getById(1L).orElse(null));
    }
}
