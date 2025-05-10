package testentity;

import org.junit.jupiter.api.Test;
import org.station.entity.Car;
import org.station.entity.Client;

import static org.junit.jupiter.api.Assertions.*;

public class TestClient {

    @Test
    public void testConstructorAndGetters() {
        Car car = new Car("Toyota", 2020, 15000.0, 5);
        Client client = new Client("СТО Київ", "Іван Іванов", "123456789", "Не працює двигун", car);

        assertEquals("СТО Київ", client.getServiceStationName());
        assertEquals("Іван Іванов", client.getFullName());
        assertEquals("123456789", client.getPhoneNumber());
        assertEquals("Не працює двигун", client.getProblem());
        assertEquals(car, client.getCar());
    }

    @Test
    public void testSetters() {
        Client client = new Client();
        Car car = new Car("BMW", 2018, 18000.0, 3);

        client.setServiceStationName("СТО Львів");
        client.setFullName("Петро Петренко");
        client.setPhoneNumber("987654321");
        client.setProblem("Стук у коробці передач");
        client.setCar(car);

        assertEquals("СТО Львів", client.getServiceStationName());
        assertEquals("Петро Петренко", client.getFullName());
        assertEquals("987654321", client.getPhoneNumber());
        assertEquals("Стук у коробці передач", client.getProblem());
        assertEquals(car, client.getCar());
    }

    @Test
    public void testToString() {
        Car car = new Car("Ford", 2019, 12000.0, 4);
        Client client = new Client("СТО Харків", "Олег Коваль", "555444333", "Проблема з гальмами", car);

        String result = client.toString();
        assertTrue(result.contains("СТО Харків"));
        assertTrue(result.contains("Олег Коваль"));
        assertTrue(result.contains("555444333"));
        assertTrue(result.contains("Проблема з гальмами"));
        assertTrue(result.contains("Ford")); // Проверка, что строка машины входит в toString
    }
}
