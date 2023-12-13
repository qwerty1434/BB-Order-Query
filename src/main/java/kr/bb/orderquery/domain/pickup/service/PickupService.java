package kr.bb.orderquery.domain.pickup.service;

import kr.bb.orderquery.client.dto.ProductInfoDto;
import kr.bb.orderquery.client.dto.StoreInfoDto;
import kr.bb.orderquery.domain.pickup.dto.PickupCreateDto;
import kr.bb.orderquery.domain.pickup.dto.PickupDetailDto;
import kr.bb.orderquery.domain.pickup.dto.PickupsForDateDto;
import kr.bb.orderquery.domain.pickup.dto.PickupsInMypageDto;
import kr.bb.orderquery.domain.pickup.entity.Pickup;
import kr.bb.orderquery.domain.pickup.handler.PickupCreator;
import kr.bb.orderquery.domain.pickup.handler.PickupReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PickupService {
    private final PickupCreator pickupCreator;
    private final PickupReader pickupReader;

    public Pickup createPickup(StoreInfoDto storeAddress, ProductInfoDto productInfo, PickupCreateDto pickupCreateDto) {
        return pickupCreator.create(storeAddress, productInfo, pickupCreateDto);
    }

    public List<PickupsInMypageDto> getPickupsForUser(Long userId) {
        return pickupReader.readByUserId(userId)
                .stream()
                .map(PickupsInMypageDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<PickupsForDateDto> getPickupsForDate(Long storeId, String pickupDate) {
        return pickupReader.readByStoreIdAndPickupDate(storeId, pickupDate)
                .stream()
                .sorted(Comparator.comparing(Pickup::getPickupDateTime))
                .map(PickupsForDateDto::fromEntity)
                .collect(Collectors.toList());
    }

    public PickupDetailDto getPickup(String pickupReservationId) {
        return PickupDetailDto.fromEntity(pickupReader.readPickup(pickupReservationId));
    }


}
