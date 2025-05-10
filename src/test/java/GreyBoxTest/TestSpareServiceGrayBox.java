package GreyBoxTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.station.entity.Spare;
import org.station.repository.SpareRepository;
import org.station.service.SpareService;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TestSpareServiceGrayBox {

    @Mock
    private SpareRepository spareRepository;

    @InjectMocks
    private SpareService spareService;

    @Test
    void testAddSpare() {
        Spare spare = new Spare();
        when(spareRepository.save(spare)).thenReturn(spare);

        Spare result = spareService.addPart(spare);

        assertThat(result).isEqualTo(spare);

        verify(spareRepository).save(spare);
    }
}
