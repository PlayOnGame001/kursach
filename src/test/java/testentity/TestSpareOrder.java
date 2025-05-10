package testentity;

import org.junit.jupiter.api.Test;
import org.station.entity.Repair;
import org.station.entity.Spare;
import org.station.entity.SpareOrder;

import static org.junit.jupiter.api.Assertions.*;

public class TestSpareOrder {

    @Test
    public void testSettersAndGetters() {
        Repair repair = new Repair();
        repair.setId(1L);

        Spare spare = new Spare();
        spare.setId(2L);
        spare.setName("Свеча");

        SpareOrder order = new SpareOrder();
        order.setId(100L);
        order.setRepair(repair);
        order.setSpare(spare);
        order.setQuantity(5);
        order.setTotalPrice(250.0);

        assertEquals(100L, order.getId());
        assertEquals(repair, order.getRepair());
        assertEquals(spare, order.getSpare());
        assertEquals(5, order.getQuantity());
        assertEquals(250.0, order.getTotalPrice());
    }

    @Test
    public void testTotalPriceCalculation() {
        SpareOrder order = new SpareOrder();
        order.setQuantity(4);
        order.setTotalPrice(400.0);

        assertEquals(400.0, order.getTotalPrice());
    }
}
