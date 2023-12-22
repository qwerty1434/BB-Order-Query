package kr.bb.orderquery.domain.pickup.handler;

import bloomingblooms.domain.pickup.PickupCreateDto;
import bloomingblooms.domain.store.StoreNameAndAddressDto;
import bloomingblooms.domain.product.ProductInfoDto;
import kr.bb.orderquery.domain.pickup.entity.Pickup;
import kr.bb.orderquery.domain.pickup.repository.PickupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PickupCreator {
    private final PickupRepository pickupRepository;

    public Pickup create(StoreNameAndAddressDto storeAddress, PickupCreateDto pickupCreateDto) {
        Pickup pickup = Pickup.builder()
                .pickupReservationId(pickupCreateDto.getPickupReservationId())
                .pickupDateTime(combineDateAndTime(pickupCreateDto.getPickupDate(), pickupCreateDto.getPickupTime()))
                .userId(pickupCreateDto.getUserId())
                .pickupDate(pickupCreateDto.getPickupDate().toString())
                .pickupTime(pickupCreateDto.getPickupTime())
                .reservationCode(UUID.randomUUID().toString())
                .storeId(pickupCreateDto.getStoreId())
                .storeAddress(storeAddress.getStoreAddress())
                .storeName(storeAddress.getStoreName())
                .productThumbnail(pickupCreateDto.getProductThumbnail())
                .productName(pickupCreateDto.getProductName())
                .unitPrice(pickupCreateDto.getUnitPrice())
                .ordererName(pickupCreateDto.getOrdererName())
                .ordererPhoneNumber(pickupCreateDto.getOrdererPhoneNumber())
                .ordererEmail(pickupCreateDto.getOrdererEmail())
                .quantity(pickupCreateDto.getQuantity())
                .totalOrderPrice(pickupCreateDto.getTotalOrderPrice())
                .totalDiscountPrice(pickupCreateDto.getTotalDiscountPrice())
                .actualPrice(pickupCreateDto.getActualPrice())
                .paymentDateTime(pickupCreateDto.getPaymentDateTime())
                .reservationStatus(pickupCreateDto.getReservationStatus())
                .reviewStatus(pickupCreateDto.getReviewStatus())
                .cardStatus(pickupCreateDto.getCardStatus())
                .build();

        return pickupRepository.save(pickup);

    }

    private LocalDateTime combineDateAndTime(LocalDate date, String time) {
        LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        return LocalDateTime.of(date,localTime);
    }



}
