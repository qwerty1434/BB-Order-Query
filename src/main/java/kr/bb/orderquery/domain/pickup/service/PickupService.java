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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.partitioningBy;

@Service
@RequiredArgsConstructor
public class PickupService {
    private final PickupCreator pickupCreator;
    private final PickupReader pickupReader;
    private final PickupManager pickupManager;

    public Pickup createPickup(StoreNameAndAddressDto storeAddress, PickupCreateDto pickupCreateDto) {
        return pickupCreator.create(storeAddress, pickupCreateDto);
    }



    public Page<PickupsInMypageDto> getPickupsForUser(Long userId, Pageable pageable, LocalDateTime now) {
        List<Pickup> contents = pickupReader.readByUserId(userId);
        List<Pickup> sortedPickups = sortAroundToday(contents, now);
        List<Pickup> slicedPickups = sliceList(sortedPickups, pageable);
        Long count = pickupReader.userPickupCount();

        List<PickupsInMypageDto> pickupsInMyPageDtos = slicedPickups.stream()
                .map(PickupsInMypageDto::fromEntity)
                .collect(Collectors.toList());
        return new PageImpl<>(pickupsInMyPageDtos, pageable, count);
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

    /*
     * now포함 이후의 값은 오름차순, now이전값은 내림차순으로 정렬
     * 오늘이 13일이고 데이터가 [11,12,13,14,15,16]이라면
     * [13,14,15,16,12,11]로 정렬됩니다
     */
    private List<Pickup> sortAroundToday(List<Pickup> pickups, LocalDateTime now) {
        Map<Boolean, List<Pickup>> collect = pickups.stream()
                .collect(partitioningBy(pickup -> pickup.getPickupDateTime().isBefore(now.withNano(0))));
        List<Pickup> afterOrEqualFromNow = collect.get(false);
        Collections.sort(afterOrEqualFromNow);
        List<Pickup> beforeFromNow = collect.get(true);
        Collections.sort(beforeFromNow,Collections.reverseOrder());
        return Stream.of(afterOrEqualFromNow, beforeFromNow)
                        .flatMap(Collection::stream)
                        .collect(Collectors.toList());
    }

    private List<Pickup> sliceList(List<Pickup> contents, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), contents.size());

        return contents.subList(start,end);
    }

}
