package testentity;

import org.junit.jupiter.api.Test;
import org.station.entity.ServiceStation;

import static org.junit.jupiter.api.Assertions.*;

public class TestServiceStation {

    @Test
    public void testConstructorAndFields() {
        ServiceStation station = new ServiceStation(1L, "СТО Київ", "вул. Перемоги 12", "380501234567");

        assertEquals(1L, station.getId());
        assertEquals("СТО Київ", station.getName());
        assertEquals("вул. Перемоги 12", station.getAddress());
        assertEquals("380501234567", station.getPhone());
    }

    @Test
    public void testSetters() {
        ServiceStation station = new ServiceStation();
        station.setId(2L);
        station.setName("СТО Львів");
        station.setAddress("вул. Шевченка 5");
        station.setPhone("380987654321");

        assertEquals(2L, station.getId());
        assertEquals("СТО Львів", station.getName());
        assertEquals("вул. Шевченка 5", station.getAddress());
        assertEquals("380987654321", station.getPhone());
    }
}
