package GreyBoxTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class TestSpareOrderServiceGrayBox {

    @Mock
    private SpareOrderRepository spareOrderRepository;

    @InjectMocks
    private SpareOrderService spareOrderService;

    @Test
    void testAddSpareOrder() {
        SpareOrder order = new SpareOrder();
        when(spareOrderRepository.save(order)).thenReturn(order);

        SpareOrder result = spareOrderService.addPartOrder(order);

        assertThat(result).isEqualTo(order);

        verify(spareOrderRepository).save(order);
    }
}
