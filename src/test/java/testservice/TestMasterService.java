package testservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.station.entity.Master;
import org.station.repository.MasterRepository;
import org.station.service.MasterService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestMasterService {
    private MasterRepository repository;
    private MasterService service;

    @BeforeEach
    void setup() {
        repository = mock(MasterRepository.class);
        service = new MasterService(repository);
    }

    @Test
    void testAddMaster() {
        Master master = new Master();
        service.addMaster(master);
        verify(repository).save(master);
    }

    @Test
    void testRemoveMasterSuccess() {
        when(repository.findById(1L)).thenReturn(Optional.of(new Master()));
        assertTrue(service.removeMaster(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void testRemoveMasterFail() {
        when(repository.findById(2L)).thenReturn(Optional.empty());
        assertFalse(service.removeMaster(2L));
    }

    @Test
    void testGetAllMasters() {
        when(repository.findAll()).thenReturn(List.of(new Master()));
        assertEquals(1, service.getAllMasters().size());
    }

    @Test
    void testGetMasterById() {
        Master master = new Master();
        when(repository.findById(1L)).thenReturn(Optional.of(master));
        assertEquals(master, service.getMasterById(1L).orElse(null));
    }
}