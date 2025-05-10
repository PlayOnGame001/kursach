package testservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.station.entity.Repair;
import org.station.repository.RepairRepository;
import org.station.service.RepairService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestRepairService {
    private RepairRepository repository;
    private RepairService service;

    @BeforeEach
    void setup() {
        repository = mock(RepairRepository.class);
        service = new RepairService(repository);
    }

    @Test
    void testAddRepair() {
        Repair repair = new Repair();
        service.addRepair(repair);
        verify(repository).save(repair);
    }

    @Test
    void testRemoveRepair() {
        assertTrue(service.removeRepair(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void testGetAllRepairs() {
        when(repository.findAll()).thenReturn(List.of(new Repair()));
        assertEquals(1, service.getAllRepairs().size());
    }

    @Test
    void testGetRepairById() {
        Repair repair = new Repair();
        when(repository.findById(1L)).thenReturn(Optional.of(repair));
        assertEquals(repair, service.getRepairById(1L).orElse(null));
    }
}

