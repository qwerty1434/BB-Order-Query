package kr.bb.orderquery.domain.pickup.service;

import bloomingblooms.domain.pickup.PickupCreateDto;
import bloomingblooms.domain.store.StoreNameAndAddressDto;
import kr.bb.orderquery.domain.pickup.dto.PickupDetailDto;
import kr.bb.orderquery.domain.pickup.dto.PickupsForDateDto;
import kr.bb.orderquery.domain.pickup.dto.PickupsInMypageDto;
import kr.bb.orderquery.domain.pickup.entity.Pickup;
import kr.bb.orderquery.domain.pickup.handler.PickupCreator;
import kr.bb.orderquery.domain.pickup.handler.PickupManager;
import kr.bb.orderquery.domain.pickup.handler.PickupReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PickupService {
    private final PickupCreator pickupCreator;
    private final PickupReader pickupReader;
    private final PickupManager pickupManager;

    public Pickup createPickup(StoreNameAndAddressDto storeAddress, PickupCreateDto pickupCreateDto) {
        return pickupCreator.create(storeAddress, pickupCreateDto);
    }

    public Page<PickupsInMypageDto> getPickupsForUser(Long userId, Pageable pageable) {
        Page<Pickup> pickups = pickupReader.readByUserId(userId, pageable);
        List<PickupsInMypageDto> pickupsInMyPageDtos = pickups.stream()
                .map(PickupsInMypageDto::fromEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(pickupsInMyPageDtos, pickups.getPageable(), pickups.getTotalElements());
    }

    public List<PickupsForDateDto> getPickupsForDate(Long storeId, String pickupDate) {
        return pickupReader.readByStoreIdAndPickupDate(storeId, pickupDate)
                .stream()
                .sorted(Comparator.comparing(Pickup::getPickupDateTime))
                .map(PickupsForDateDto::fromEntity)
                .collect(Collectors.toList());
    }

    public PickupDetailDto getPickup(String pickupReservationId) {
        return PickupDetailDto.fromEntity(pickupReader.read(pickupReservationId));
    }

    public List<Pickup> getPickupByStoreId(Long storeId) {
        return pickupReader.readByStoreId(storeId);
    }

    public void updateCardStatus(String subscriptionId, String cardStatus) {
        Pickup pickup = pickupReader.read(subscriptionId);
        pickupManager.changeCardStatus(pickup, cardStatus);
    }

    public void updateReviewStatus(String subscriptionId, String reviewStatus) {
        Pickup pickup = pickupReader.read(subscriptionId);
        pickupManager.changeReviewStatus(pickup, reviewStatus);
    }

    public void updateReservationStatus(String subscriptionId, String reservationStatus) {
        Pickup pickup = pickupReader.read(subscriptionId);
        pickupManager.changeReservationStatus(pickup, reservationStatus);
    }


}
