package kr.bb.orderquery.domain.subscription.service;

import kr.bb.orderquery.client.dto.ProductInfoDto;
import kr.bb.orderquery.domain.subscription.dto.SubscriptionCreateDto;
import kr.bb.orderquery.domain.subscription.dto.SubscriptionDetailDto;
import kr.bb.orderquery.domain.subscription.dto.SubscriptionForDateDto;
import kr.bb.orderquery.domain.subscription.dto.SubscriptionForUserDto;
import kr.bb.orderquery.domain.subscription.entity.Subscription;
import kr.bb.orderquery.domain.subscription.handler.SubscriptionCreator;
import kr.bb.orderquery.domain.subscription.handler.SubscriptionReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionCreator subscriptionCreator;
    private final SubscriptionReader subscriptionReader;

    public Subscription createSubscription(ProductInfoDto productInfo, SubscriptionCreateDto subscriptionCreateDto) {
        return subscriptionCreator.create(productInfo, subscriptionCreateDto);
    }

    public List<SubscriptionForUserDto> getSubscriptionsOfUser(Long userId) {
        return subscriptionReader.readByUserId(userId)
                .stream()
                .filter(Predicate.not(Subscription::getIsUnsubscribed))
                .map(SubscriptionForUserDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<SubscriptionForDateDto> getSubscriptionsForDate(Long storeId, String nextDeliveryDate) {
        return subscriptionReader.readByStoreIdAndNextDeliveryDate(storeId, nextDeliveryDate)
                .stream()
                .map(SubscriptionForDateDto::fromEntity)
                .collect(Collectors.toList());
    }

    public SubscriptionDetailDto getSubscription(String subscriptionId) {
        return SubscriptionDetailDto.fromEntity(subscriptionReader.readSubscription(subscriptionId));
    }

}
