package GreyBoxTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.station.entity.RepairType;
import org.station.repository.RepairTypeRepository;
import org.station.service.RepairTypeService;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TestRepairTypeServiceGrayBox {

    @Mock
    private RepairTypeRepository repairTypeRepository;

    @InjectMocks
    private RepairTypeService repairTypeService;

    @Test
    void testAddRepairType() {
        RepairType type = new RepairType();
        when(repairTypeRepository.save(type)).thenReturn(type);

        RepairType result = repairTypeService.addRepairType(type);

        assertThat(result).isEqualTo(type);

        verify(repairTypeRepository).save(type);
    }
}
