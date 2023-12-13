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
public class PickupsForDateDto {
    private String pickupReservationId;
    private String reservationCode;
    private String productThumbnailImage;
    private String productName;
    private Integer count;
    private Long orderPickupTotalAmount;
    private String nickname;
    private String phoneNumber;
    private String pickupDate;
    private String pickupTime;

    public static PickupsForDateDto fromEntity(Pickup pickup) {
        return PickupsForDateDto.builder()
                .pickupReservationId(pickup.getPickupReservationId())
                .reservationCode(pickup.getReservationCode())
                .productThumbnailImage(pickup.getProductThumbnail())
                .productName(pickup.getProductName())
                .count(pickup.getQuantity())
                .orderPickupTotalAmount(pickup.getActualPrice())
                .nickname(pickup.getOrdererName())
                .phoneNumber(pickup.getOrdererPhoneNumber())
                .pickupDate(pickup.getPickupDate())
                .pickupTime(pickup.getPickupTime())
                .build();
    }
}
