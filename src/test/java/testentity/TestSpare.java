package testentity;

import org.junit.jupiter.api.Test;
import org.station.entity.RepairType;
import org.station.entity.Spare;

import static org.junit.jupiter.api.Assertions.*;

public class TestSpare {

    @Test
    public void testConstructorWithoutRepairType() {
        Spare spare = new Spare("Свеча", 10);

        assertEquals("Свеча", spare.getName());
        assertEquals(10, spare.getQuantity());
        assertNull(spare.getRepairType());
    }

    @Test
    public void testSettersAndGetters() {
        RepairType repairType = new RepairType();
        repairType.setId(1L);
        repairType.setName("Двигун");

        Spare spare = new Spare();
        spare.setId(5L);
        spare.setName("Масло");
        spare.setQuantity(3);
        spare.setRepairType(repairType);

        assertEquals(5L, spare.getId());
        assertEquals("Масло", spare.getName());
        assertEquals(3, spare.getQuantity());
        assertEquals(repairType, spare.getRepairType());
    }
}
