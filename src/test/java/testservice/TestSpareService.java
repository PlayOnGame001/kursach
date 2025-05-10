package testservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.station.entity.Spare;
import org.station.repository.SpareRepository;
import org.station.service.SpareService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestSpareService {
    private SpareRepository repository;
    private SpareService service;

    @BeforeEach
    void setup() {
        repository = mock(SpareRepository.class);
        service = new SpareService(repository);
    }

    @Test
    void testAddPart() {
        Spare spare = new Spare();
        service.addPart(spare);
        verify(repository).save(spare);
    }

    @Test
    void testRemovePart() {
        assertTrue(service.removePart(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void testGetAllParts() {
        when(repository.findAll()).thenReturn(List.of(new Spare()));
        assertEquals(1, service.getAllParts().size());
    }

    @Test
    void testGetPartById() {
        Spare spare = new Spare();
        when(repository.findById(1L)).thenReturn(Optional.of(spare));
        assertEquals(spare, service.getPartById(1L).orElse(null));
    }

    @Test
    void testGetPartByName() {
        Spare spare = new Spare();
        spare.setName("Масло");
        when(repository.findAll()).thenReturn(List.of(spare));
        assertTrue(service.getPartByName("масло").isPresent());
    }

    @Test
    void testUpdatePart() {
        Spare spare = new Spare();
        service.updatePart(spare);
        verify(repository).save(spare);
    }
}
