package testservice;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.station.entity.Car;
import org.station.repository.CarRepository;
import org.station.service.CarService;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
public class TestCarService {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarService carService;

    @Test
    void testAddCar() {
        Car car = new Car("Toyota", 2021, 25000.0, 5);
        carService.addCar(car);
        verify(carRepository, times(1)).save(car);
    }

    @Test
    void testRemoveCar() {
        long id = 1L;
        boolean result = carService.removeCar(id);
        verify(carRepository, times(1)).deleteById(id);
        assertThat(result).isTrue();
    }

    @Test
    void testGetAllCars() {
        List<Car> mockList = Arrays.asList(new Car("BMW", 2020, 20000, 6));
        when(carRepository.findAll()).thenReturn(mockList);

        List<Car> result = carService.getAllCars();

        assertThat(result).isEqualTo(mockList);
    }

    @Test
    void testGetCarById() {
        Car car = new Car("Mazda", 2022, 18000, 4);
        when(carRepository.findById(1L)).thenReturn(Optional.of(car));

        Optional<Car> found = carService.getCarById(1L);
        assertThat(found).isPresent();
        assertThat(found.get().getBrand()).isEqualTo("Mazda");
    }

    @Test
    void testGetCarsByBrand() {
        List<Car> mockList = List.of(new Car("Audi", 2019, 23000, 7));
        when(carRepository.getCarsByBrand("Audi")).thenReturn(mockList);

        List<Car> result = carService.getCarsByBrand("Audi");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getBrand()).isEqualTo("Audi");
    }

    @Test
    void testGetAverageRepairDuration() {
        List<Car> cars = List.of(
                new Car("Car1", 2020, 10000, 5),
                new Car("Car2", 2021, 12000, 3)
        );
        when(carRepository.findAll()).thenReturn(cars);

        double average = carService.getAverageRepairDuration();
        assertThat(average).isEqualTo(4.0);
    }

    @Test
    void testGetAverageRepairDurationEmpty() {
        when(carRepository.findAll()).thenReturn(List.of());
        double avg = carService.getAverageRepairDuration();
        assertThat(avg).isZero();
    }
}
