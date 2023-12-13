package kr.bb.orderquery.domain.pickup.handler;

import kr.bb.orderquery.domain.pickup.entity.Pickup;
import kr.bb.orderquery.domain.pickup.repository.PickupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PickupManager {
    private final PickupRepository pickupRepository;

    public void changeCardStatus(Pickup pickup, String cardStatus) {
        pickup.setCardStatus(cardStatus);
        pickupRepository.save(pickup);
    }

    public void changeReviewStatus(Pickup pickup, String reviewStatus) {
        pickup.setReviewStatus(reviewStatus);
        pickupRepository.save(pickup);
    }

    public void changeReservationStatus(Pickup pickup, String reservationStatus) {
        pickup.setReviewStatus(reservationStatus);
        pickupRepository.save(pickup);
    }

}
