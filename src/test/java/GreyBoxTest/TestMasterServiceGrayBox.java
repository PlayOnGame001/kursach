package GreyBoxTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.station.entity.Master;
import org.station.repository.MasterRepository;
import org.station.service.MasterService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestMasterServiceGrayBox {

    private MasterRepository masterRepository;
    private MasterService masterService;

    @BeforeEach
    public void setUp() {
        masterRepository = mock(MasterRepository.class);
        masterService = new MasterService(masterRepository);
    }

    @Test
    public void testAddMaster() {
        Master master = new Master();
        masterService.addMaster(master);
        verify(masterRepository).save(master);
    }

    @Test
    public void testRemoveMasterFound() {
        when(masterRepository.findById(1L)).thenReturn(Optional.of(new Master()));
        boolean removed = masterService.removeMaster(1L);
        assertTrue(removed);
        verify(masterRepository).deleteById(1L);
    }

    @Test
    public void testRemoveMasterNotFound() {
        when(masterRepository.findById(1L)).thenReturn(Optional.empty());
        boolean removed = masterService.removeMaster(1L);
        assertFalse(removed);
        verify(masterRepository, never()).deleteById(1L);
    }

    @Test
    public void testGetAllMasters() {
        List<Master> masters = List.of(new Master(), new Master());
        when(masterRepository.findAll()).thenReturn(masters);
        assertEquals(2, masterService.getAllMasters().size());
    }

    @Test
    public void testGetMasterById() {
        Master master = new Master();
        when(masterRepository.findById(2L)).thenReturn(Optional.of(master));
        Optional<Master> result = masterService.getMasterById(2L);
        assertTrue(result.isPresent());
        assertEquals(master, result.get());
    }
}
