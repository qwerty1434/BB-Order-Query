package kr.bb.orderquery.domain.subscription.dto;

import kr.bb.orderquery.domain.subscription.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionForUserDto {
    private String subscriptionId;
    private String subscriptionProductName;
    private String subscriptionProductThumbnail;
    private LocalDate paymentDate;
    private LocalDate deliveryDate;

    public static SubscriptionForUserDto fromEntity(Subscription subscription) {
        return SubscriptionForUserDto.builder()
                .subscriptionId(subscription.getSubscriptionId())
                .subscriptionProductName(subscription.getProductName())
                .subscriptionProductThumbnail(subscription.getProductThumbnail())
                .paymentDate(subscription.getNextPaymentDate())
                .deliveryDate(LocalDate.parse(subscription.getNextDeliveryDate()))
                .build();
    }
}
