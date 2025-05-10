package org.station.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.station.entity.Car;
import org.station.repository.CarRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarService{
    private final CarRepository carRepository;

    public void addCar(Car car) {
        carRepository.save(car);
    }

    public boolean removeCar(long ID) {
        carRepository.deleteById(ID);
        return true;
    }

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Optional<Car>  getCarById(long id) {
        return carRepository.findById(id);
    }

    public List<Car> getCarsByBrand(String brand) {
        return carRepository.getCarsByBrand(brand);
    }

    public double getAverageRepairDuration(){
        List<Car> cars = carRepository.findAll();

        if (cars.isEmpty()) {
            return 0;
        }

        return cars.stream()
                .mapToDouble(Car::getRepairDuration)
                .average()
                .orElse(0.0);
    }
}

