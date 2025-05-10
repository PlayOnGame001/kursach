package GreyBoxTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.station.entity.Car;
import org.station.repository.CarRepository;
import org.station.service.CarService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestCarServiceGrayBox {

    private CarRepository carRepository;
    private CarService carService;

    @BeforeEach
    public void setUp() {
        carRepository = mock(CarRepository.class);
        carService = new CarService(carRepository);
    }

    @Test
    public void testAddCarDelegatesToRepository() {
        Car car = new Car("Ford", 2020, 0.0, 10);
        carService.addCar(car);

        verify(carRepository, times(1)).save(car);
    }

    @Test
    public void testRemoveCarAlwaysReturnsTrue() {
        doNothing().when(carRepository).deleteById(1L);
        boolean result = carService.removeCar(1L);
        assertTrue(result);
        verify(carRepository).deleteById(1L);
    }

    @Test
    public void testGetCarsByBrandReturnsCorrectList() {
        List<Car> cars = List.of(new Car("Audi", 2021, 0.0, 5));
        when(carRepository.getCarsByBrand("Audi")).thenReturn(cars);

        List<Car> result = carService.getCarsByBrand("Audi");

        assertEquals(1, result.size());
        assertEquals("Audi", result.get(0).getBrand());
    }

    @Test
    public void testGetAverageRepairDurationWithCars() {
        List<Car> cars = Arrays.asList(
                new Car("BMW", 2020, 0.0, 4),
                new Car("Opel", 2021, 0.0, 6)
        );
        when(carRepository.findAll()).thenReturn(cars);

        double avg = carService.getAverageRepairDuration();

        assertEquals(5.0, avg);
    }

    @Test
    public void testGetAverageRepairDurationWhenEmpty() {
        when(carRepository.findAll()).thenReturn(Collections.emptyList());

        double avg = carService.getAverageRepairDuration();

        assertEquals(0.0, avg);
    }

    @Test
    public void testGetCarByIdFound() {
        Car car = new Car("Lada", 2000, 0.0, 3);
        when(carRepository.findById(42L)).thenReturn(Optional.of(car));

        Optional<Car> result = carService.getCarById(42L);
        assertTrue(result.isPresent());
        assertEquals("Lada", result.get().getBrand());
    }

    @Test
    public void testGetCarByIdNotFound() {
        when(carRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Car> result = carService.getCarById(99L);
        assertFalse(result.isPresent());
    }
}
