package kr.bb.orderquery.domain.pickup.dto;

import kr.bb.orderquery.domain.pickup.entity.Pickup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupsInMypageDto {
    private String key;
    private String reservationCode;
    private String productThumbnail;
    private String productName;
    private Integer quantity;
    private String storeAddress;
    private Long actualPrice;
    private String reservationStatus;
    private String pickupDate;
    private String pickupTime;

    public static PickupsInMypageDto fromEntity(Pickup pickup) {
        return PickupsInMypageDto.builder()
                .key(pickup.getPickupReservationId())
                .reservationCode(pickup.getReservationCode())
                .productThumbnail(pickup.getProductThumbnail())
                .productName(pickup.getProductName())
                .quantity(pickup.getQuantity())
                .storeAddress(pickup.getStoreAddress())
                .actualPrice(pickup.getActualPrice())
                .reservationStatus(pickup.getReservationStatus())
                .pickupDate(pickup.getPickupDate())
                .pickupTime(pickup.getPickupTime())
                .build();
    }

}
