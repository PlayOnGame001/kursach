package testentity;

import org.junit.jupiter.api.Test;
import org.station.entity.Master;

import static org.junit.jupiter.api.Assertions.*;

public class TestMaster {

    @Test
    public void testConstructorAndGetters() {
        Master master = new Master("СТО Харків", "Андрій Петров", "0501234567", "Харків, вул. Свободи", "Кузовний ремонт");

        assertEquals("СТО Харків", master.getServiceStationName());
        assertEquals("Андрій Петров", master.getFullName());
        assertEquals("0501234567", master.getPhoneNumber());
        assertEquals("Харків, вул. Свободи", master.getAddress());
        assertEquals("Кузовний ремонт", master.getSpecialization());
    }

    @Test
    public void testSetters() {
        Master master = new Master();
        master.setId(10L);
        master.setServiceStationName("СТО Львів");
        master.setFullName("Іван Технік");
        master.setPhoneNumber("0679876543");
        master.setAddress("Львів, вул. Личаківська");
        master.setSpecialization("Діагностика");

        assertEquals(10L, master.getId());
        assertEquals("СТО Львів", master.getServiceStationName());
        assertEquals("Іван Технік", master.getFullName());
        assertEquals("0679876543", master.getPhoneNumber());
        assertEquals("Львів, вул. Личаківська", master.getAddress());
        assertEquals("Діагностика", master.getSpecialization());
    }

    @Test
    public void testToString() {
        Master master = new Master("СТО Дніпро", "Марко Носаль", "0731122334", "Дніпро, вул. Центральна", "Електрика");
        master.setId(5L);

        String expected = "5 Марко Носаль СТО Дніпро 0731122334 Дніпро, вул. Центральна Електрика";
        assertEquals(expected, master.toString());
    }
}
