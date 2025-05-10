package testentity;

import org.junit.jupiter.api.Test;
import org.station.entity.Car;

import static org.junit.jupiter.api.Assertions.*;

public class TestCar {

    @Test
    public void testConstructorAndGetters() {
        Car car = new Car("Toyota", 2020, 15000.0, 7);

        assertEquals("Toyota", car.getBrand());
        assertEquals(2020, car.getReleaseYear());
        assertEquals(15000.0, car.getPrice());
        assertEquals(7, car.getRepairDuration());
    }

    @Test
    public void testSetters() {
        Car car = new Car();    
        car.setId(1L);
        car.setBrand("BMW");
        car.setReleaseYear(2022);
        car.setPrice(20000.0);
        car.setRepairDuration(5);

        assertEquals(1L, car.getId());
        assertEquals("BMW", car.getBrand());
        assertEquals(2022, car.getReleaseYear());
        assertEquals(20000.0, car.getPrice());
        assertEquals(5, car.getRepairDuration());
    }

    @Test
    public void testToString() {
        Car car = new Car("Audi", 2021, 30000.0, 10);
        car.setId(3L);

        String expected = "3 Audi 2021 30000.0 10";
        assertEquals(expected, car.toString());
    }
}
