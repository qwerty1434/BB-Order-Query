package kr.bb.orderquery.domain.subscription.dto;

import kr.bb.orderquery.domain.subscription.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionForDateDto {
    private String storeSubscriptionId;
    private String subscriptionCode;
    private String productName;
    private String productThumbnailImage;
    private String deliveryRecipientName;
    private String deliveryRecipientPhoneNumber;
    private String deliveryAddress;
    private Long productPrice;

    public static SubscriptionForDateDto fromEntity(Subscription subscription) {
        return SubscriptionForDateDto.builder()
                .storeSubscriptionId(subscription.getSubscriptionId())
                .subscriptionCode(subscription.getSubscriptionCode())
                .productName(subscription.getProductName())
                .productThumbnailImage(subscription.getProductThumbnail())
                .deliveryRecipientName(subscription.getRecipientName())
                .deliveryRecipientPhoneNumber(subscription.getRecipientPhoneNumber())
                .deliveryAddress(subscription.getDeliveryAddress())
                .productPrice(subscription.getActualPrice())
                .build();
    }
}
