package testentity;

import org.junit.jupiter.api.Test;
import org.station.entity.*;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestRepair {

    @Test
    public void testConstructorAndGettersSetters() {
        Car car = new Car("Toyota", 2020, 20000, 5);
        car.setId(1L);

        Master master = new Master("СТО Львів", "Андрій", "0661234567", "Львів, вул. Головна", "Двигуни");
        master.setId(2L);

        RepairType type = new RepairType();
        type.setId(3L);
        type.setName("Заміна масла");
        type.setDescription("Повна заміна масла");

        LocalDate start = LocalDate.of(2024, 5, 1);
        LocalDate end = LocalDate.of(2024, 5, 3);

        Repair repair = new Repair();
        repair.setId(100L);
        repair.setCar(car);
        repair.setMaster(master);
        repair.setRepairType(type);
        repair.setStartDate(start);
        repair.setEndDate(end);
        repair.setDescription("Тестовий ремонт");

        assertEquals(100L, repair.getId());
        assertEquals(car, repair.getCar());
        assertEquals(master, repair.getMaster());
        assertEquals(type, repair.getRepairType());
        assertEquals(start, repair.getStartDate());
        assertEquals(end, repair.getEndDate());
        assertEquals("Тестовий ремонт", repair.getDescription());
        assertNotNull(repair.getSpareOrders());
        assertTrue(repair.getSpareOrders().isEmpty());
    }

    @Test
    public void testSpareOrdersManagement() {
        Repair repair = new Repair();
        SpareOrder order1 = new SpareOrder();
        order1.setId(1L);
        order1.setRepair(repair);

        SpareOrder order2 = new SpareOrder();
        order2.setId(2L);
        order2.setRepair(repair);

        repair.getSpareOrders().add(order1);
        repair.getSpareOrders().add(order2);

        List<SpareOrder> orders = repair.getSpareOrders();
        assertEquals(2, orders.size());
        assertEquals(1L, orders.get(0).getId());
        assertEquals(2L, orders.get(1).getId());
    }
}
