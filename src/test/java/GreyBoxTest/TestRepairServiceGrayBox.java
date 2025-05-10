package GreyBoxTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.station.entity.Repair;
import org.station.repository.RepairRepository;
import org.station.service.RepairService;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TestRepairServiceGrayBox {

    @Mock
    private RepairRepository repairRepository;

    @InjectMocks
    private RepairService repairService;

    @Test
    void testAddRepair() {
        Repair repair = new Repair();
        when(repairRepository.save(repair)).thenReturn(repair);

        Repair saved = repairService.addRepair(repair);

        assertThat(saved).isEqualTo(repair);

        verify(repairRepository).save(repair);
    }
}
