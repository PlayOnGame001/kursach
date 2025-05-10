package testservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.station.entity.RepairType;
import org.station.repository.RepairTypeRepository;
import org.station.service.RepairTypeService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestRepairTypeService {
    private RepairTypeRepository repository;
    private RepairTypeService service;

    @BeforeEach
    void setup() {
        repository = mock(RepairTypeRepository.class);
        service = new RepairTypeService(repository);
    }

    @Test
    void testAddRepairType() {
        RepairType type = new RepairType();
        service.addRepairType(type);
        verify(repository).save(type);
    }

    @Test
    void testRemoveRepairType() {
        assertTrue(service.removeRepairType(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void testGetAllRepairTypes() {
        when(repository.findAll()).thenReturn(List.of(new RepairType()));
        assertEquals(1, service.getAllRepairTypes().size());
    }

    @Test
    void testGetRepairTypeById() {
        RepairType type = new RepairType();
        when(repository.findById(1L)).thenReturn(Optional.of(type));
        assertEquals(type, service.getRepairTypeById(1L).orElse(null));
    }
}
