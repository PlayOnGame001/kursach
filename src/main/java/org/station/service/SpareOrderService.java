package org.station.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.station.entity.SpareOrder;
import org.station.repository.SpareOrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SpareOrderService {
    private final SpareOrderRepository spareOrderRepository;

    public SpareOrder addPartOrder(SpareOrder spareOrder) {
        return spareOrderRepository.save(spareOrder);
    }

    public boolean removePartOrder(Long id) {
        spareOrderRepository.deleteById(id);
        return true;
    }

    public List<SpareOrder> getAllPartOrders() {
        return spareOrderRepository.findAll();
    }

    public Optional<SpareOrder> getPartOrderById(Long id) {
        return spareOrderRepository.findById(id);
    }

    public List<SpareOrder> getOrdersByRepairId(Long repairId) {
        return spareOrderRepository.findByRepairId(repairId);
    }


}
