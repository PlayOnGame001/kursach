package GreyBoxTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.station.entity.ServiceStation;
import org.station.repository.ServiceStationRepository;
import org.station.service.ServiceStationService;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TestServiceStationServiceGrayBox {

    @Mock
    private ServiceStationRepository stationRepository;

    @InjectMocks
    private ServiceStationService stationService;

    @Test
    void testAddStation() {
        ServiceStation station = new ServiceStation();
        when(stationRepository.save(station)).thenReturn(station);

        ServiceStation result = stationService.addStation(station);

        assertThat(result).isEqualTo(station);

        verify(stationRepository).save(station);
    }
}
