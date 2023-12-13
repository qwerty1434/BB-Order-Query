package kr.bb.orderquery.domain.subscription.dto;

import kr.bb.orderquery.domain.subscription.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionDetailDto {
    private String productName;
    private String productThumbnail;
    private Long unitPrice;
    private Integer quantity;
    private LocalDateTime paymentDateTime;
    private Long totalOrderPrice;
    private Long totalDiscountPrice;
    private Long deliveryPrice;
    private Long actualPrice;
    private String reviewStatus;
    private String ordererName;
    private String ordererPhoneNumber;
    private String ordererEmail;
    private String recipientName;
    private String recipientPhoneNumber;
    private String storeName;
    private String zipcode;
    private String roadName;
    private String addressDetail;
    private LocalDate nextPaymentDate;
    private LocalDate nextDeliveryDate;
    private String deliveryRequest;

    public static SubscriptionDetailDto fromEntity(Subscription subscription) {
        return SubscriptionDetailDto.builder()
                .productName(subscription.getProductName())
                .productThumbnail(subscription.getProductThumbnail())
                .unitPrice(subscription.getUnitPrice())
                .quantity(subscription.getQuantity())
                .paymentDateTime(subscription.getPaymentDateTime())
                .totalOrderPrice(subscription.getTotalOrderPrice())
                .totalDiscountPrice(subscription.getTotalDiscountPrice())
                .deliveryPrice(subscription.getDeliveryPrice())
                .actualPrice(subscription.getActualPrice())
                .reviewStatus(subscription.getReviewStatus())
                .ordererName(subscription.getOrdererName())
                .ordererPhoneNumber(subscription.getOrdererPhoneNumber())
                .ordererEmail(subscription.getOrdererEmail())
                .recipientName(subscription.getRecipientName())
                .recipientPhoneNumber(subscription.getRecipientPhoneNumber())
                .storeName(subscription.getStoreName())
                .zipcode(subscription.getZipcode())
                .roadName(subscription.getRoadName())
                .addressDetail(subscription.getAddressDetail())
                .nextPaymentDate(subscription.getNextPaymentDate())
                .nextDeliveryDate(LocalDate.parse(subscription.getNextDeliveryDate()))
                .deliveryRequest(subscription.getDeliveryRequest())
                .build();
    }
}
