package kr.bb.orderquery.domain.pickup.handler;

import kr.bb.orderquery.domain.pickup.entity.Pickup;
import kr.bb.orderquery.domain.pickup.exception.PickupNotFoundException;
import kr.bb.orderquery.domain.pickup.repository.PickupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
public class PickupReader {
    private final PickupRepository pickupRepository;

    public List<Pickup> readByUserId(Long userId) {
        return pickupRepository.findAllByUserIdOrderByPickupDateTimeDesc(userId);
    }

    public List<Pickup> readByStoreIdAndPickupDate(Long storeId, String pickupDate) {
        return pickupRepository.findAllByStoreIdAndPickupDate(storeId, pickupDate);
    }

    public Pickup readPickup(String pickup) {
        return pickupRepository.findById(pickup).orElseThrow(PickupNotFoundException::new);
    }
}
