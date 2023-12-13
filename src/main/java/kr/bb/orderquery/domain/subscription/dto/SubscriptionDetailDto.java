package kr.bb.orderquery.domain.subscription.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
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
    private String cardStatus;
    private String ordererName;
    private String ordererPhoneNumber;
    private String ordererEmail;
    private String recipientName;
    private String recipientPhoneNumber;
    private String deliveryAddress;
    private LocalDate nextPaymentDate;
    private LocalDate nextDeliveryDate;

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
                .cardStatus(subscription.getCardStatus())
                .ordererName(subscription.getOrdererName())
                .ordererPhoneNumber(subscription.getOrdererPhoneNumber())
                .ordererEmail(subscription.getOrdererEmail())
                .recipientName(subscription.getRecipientName())
                .recipientPhoneNumber(subscription.getRecipientPhoneNumber())
                .deliveryAddress(subscription.getDeliveryAddress())
                .nextPaymentDate(subscription.getNextPaymentDate())
                .nextDeliveryDate(LocalDate.parse(subscription.getNextDeliveryDate()))
                .build();
    }
}
