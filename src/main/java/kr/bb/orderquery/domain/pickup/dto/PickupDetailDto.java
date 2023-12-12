package kr.bb.orderquery.domain.pickup.dto;

import kr.bb.orderquery.domain.pickup.entity.Pickup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupDetailDto {
    private String productName;
    private String productThumbnail;
    private Long unitPrice;
    private Integer quantity;
    private String reservationStatus;
    private LocalDateTime paymentDateTime;
    private Long totalOrderPrice;
    private Long totalDiscountPrice;
    private Long deliveryPrice;
    private Long actualPrice;
    private String reviewStatus;
    private String cardStatus;
    private String ordererName;
    private String ordererPhoneNumber;
    private String ordererEmail;

    public static PickupDetailDto fromEntity(Pickup pickup) {
        return PickupDetailDto.builder()
                .productName(pickup.getProductName())
                .productThumbnail(pickup.getProductThumbnail())
                .unitPrice(pickup.getUnitPrice())
                .quantity(pickup.getQuantity())
                .reservationStatus(pickup.getReservationStatus())
                .paymentDateTime(pickup.getPaymentDateTime())
                .totalOrderPrice(pickup.getTotalOrderPrice())
                .totalDiscountPrice(pickup.getTotalDiscountPrice())
                .deliveryPrice(pickup.getDeliveryPrice())
                .actualPrice(pickup.getActualPrice())
                .reviewStatus(pickup.getReviewStatus())
                .cardStatus(pickup.getCardStatus())
                .ordererName(pickup.getOrdererName())
                .ordererPhoneNumber(pickup.getOrdererPhoneNumber())
                .ordererEmail(pickup.getOrdererEmail())
                .build();
    }

}
