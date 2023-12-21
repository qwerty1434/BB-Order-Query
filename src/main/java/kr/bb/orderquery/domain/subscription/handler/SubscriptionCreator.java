package kr.bb.orderquery.domain.subscription.handler;

import bloomingblooms.domain.subscription.SubscriptionCreateDto;
import bloomingblooms.domain.product.ProductInfoDto;
import kr.bb.orderquery.domain.subscription.entity.Subscription;
import kr.bb.orderquery.domain.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionCreator {
    private final SubscriptionRepository subscriptionRepository;

    public Subscription create(ProductInfoDto productInfo, SubscriptionCreateDto subscriptionCreateDto) {
        Subscription subscription = Subscription.builder()
                .subscriptionId(subscriptionCreateDto.getSubscriptionId())
                .subscriptionCode(UUID.randomUUID().toString())
                .userId(subscriptionCreateDto.getUserId())
                .storeId(productInfo.getStoreId())
                .productThumbnail(productInfo.getProductThumbnail())
                .productName(productInfo.getProductName())
                .unitPrice(productInfo.getUnitPrice())
                .quantity(subscriptionCreateDto.getQuantity())
                .ordererName(subscriptionCreateDto.getOrdererName())
                .ordererPhoneNumber(subscriptionCreateDto.getOrdererPhoneNumber())
                .ordererEmail(subscriptionCreateDto.getOrdererEmail())
                .recipientName(subscriptionCreateDto.getRecipientName())
                .recipientPhoneNumber(subscriptionCreateDto.getRecipientPhoneNumber())
                .deliveryAddress(subscriptionCreateDto.getRoadName() + " " + subscriptionCreateDto.getAddressDetail())
                .storeName(subscriptionCreateDto.getStoreName())
                .zipcode(subscriptionCreateDto.getZipcode())
                .roadName(subscriptionCreateDto.getRoadName())
                .addressDetail(subscriptionCreateDto.getAddressDetail())
                .paymentDateTime(subscriptionCreateDto.getPaymentDateTime())
                .nextDeliveryDate(subscriptionCreateDto.getNextDeliveryDate().toString())
                .nextPaymentDate(subscriptionCreateDto.getNextPaymentDate())
                .totalOrderPrice(subscriptionCreateDto.getTotalOrderPrice())
                .totalDiscountPrice(subscriptionCreateDto.getTotalDiscountPrice())
                .deliveryPrice(subscriptionCreateDto.getDeliveryPrice())
                .actualPrice(subscriptionCreateDto.getActualPrice())
                .reviewStatus(subscriptionCreateDto.getReviewStatus())
                .isUnsubscribed(false)
                .productId(subscriptionCreateDto.getProductId())
                .build();

        return subscriptionRepository.save(subscription);
    }
}
