package testservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class TestSpareOrderService {
    private SpareOrderRepository repository;
    private SpareOrderService service;

    @BeforeEach
    void setup() {
        repository = mock(SpareOrderRepository.class);
        service = new SpareOrderService(repository);
    }

    @Test
    void testAddPartOrder() {
        SpareOrder order = new SpareOrder();
        service.addPartOrder(order);
        verify(repository).save(order);
    }

    @Test
    void testRemovePartOrder() {
        assertTrue(service.removePartOrder(1L));
        verify(repository).deleteById(1L);
    }

    @Test
    void testGetAllPartOrders() {
        when(repository.findAll()).thenReturn(List.of(new SpareOrder()));
        assertEquals(1, service.getAllPartOrders().size());
    }

    @Test
    void testGetPartOrderById() {
        SpareOrder order = new SpareOrder();
        when(repository.findById(1L)).thenReturn(Optional.of(order));
        assertEquals(order, service.getPartOrderById(1L).orElse(null));
    }

    @Test
    void testGetOrdersByRepairId() {
        when(repository.findByRepairId(1L)).thenReturn(List.of(new SpareOrder()));
        assertEquals(1, service.getOrdersByRepairId(1L).size());
    }
}