package testentity;

import org.junit.jupiter.api.Test;
import org.station.entity.Repair;
import org.station.entity.RepairType;
import org.station.entity.Spare;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TestRepairType {

    @Test
    public void testConstructorAndFields() {
        RepairType repairType = new RepairType();
        repairType.setId(1L);
        repairType.setName("Заміна масла");
        repairType.setDescription("Повна заміна моторного масла");

        assertEquals(1L, repairType.getId());
        assertEquals("Заміна масла", repairType.getName());
        assertEquals("Повна заміна моторного масла", repairType.getDescription());
        assertNotNull(repairType.getRepairs());
        assertNotNull(repairType.getSpares());
        assertTrue(repairType.getRepairs().isEmpty());
        assertTrue(repairType.getSpares().isEmpty());
    }

    @Test
    public void testAddRepairsAndSpares() {
        RepairType repairType = new RepairType();

        Repair repair1 = new Repair();
        Repair repair2 = new Repair();

        Spare spare1 = new Spare();
        Spare spare2 = new Spare();

        repairType.getRepairs().add(repair1);
        repairType.getRepairs().add(repair2);

        repairType.getSpares().add(spare1);
        repairType.getSpares().add(spare2);

        List<Repair> repairs = repairType.getRepairs();
        List<Spare> spares = repairType.getSpares();

        assertEquals(2, repairs.size());
        assertEquals(2, spares.size());
    }
}
