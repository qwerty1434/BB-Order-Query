package kr.bb.orderquery.domain.subscription.service;

import bloomingblooms.domain.product.ProductInfoDto;
import bloomingblooms.domain.subscription.SubscriptionCreateDto;
import kr.bb.orderquery.domain.subscription.dto.SubscriptionDetailDto;
import kr.bb.orderquery.domain.subscription.dto.SubscriptionForDateDto;
import kr.bb.orderquery.domain.subscription.dto.SubscriptionForUserDto;
import kr.bb.orderquery.domain.subscription.entity.Subscription;
import kr.bb.orderquery.domain.subscription.handler.SubscriptionCreator;
import kr.bb.orderquery.domain.subscription.handler.SubscriptionManager;
import kr.bb.orderquery.domain.subscription.handler.SubscriptionReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubscriptionService {
    private final SubscriptionCreator subscriptionCreator;
    private final SubscriptionReader subscriptionReader;
    private final SubscriptionManager subscriptionManager;

    public Subscription createSubscription(SubscriptionCreateDto subscriptionCreateDto) {
        return subscriptionCreator.create(subscriptionCreateDto);
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
        return SubscriptionDetailDto.fromEntity(subscriptionReader.read(subscriptionId));
    }

    public List<Subscription> getSubscriptionByStoreId(Long storeId) {
        return subscriptionReader.readByStoreId(storeId);
    }

    public void updateSubscriptionDate(String subscriptionId, LocalDate nextDeliveryDate, LocalDate nextPaymentDate) {
        Subscription subscription = subscriptionReader.read(subscriptionId);
        subscriptionManager.updateNextDeliveryDate(subscription, nextDeliveryDate, nextPaymentDate);
    }

    public void unSubscribe(String subscriptionId) {
        Subscription subscription = subscriptionReader.read(subscriptionId);
        subscriptionManager.unSubscribe(subscription);
    }

    public void updateReviewStatus(String subscriptionId, String reviewStatus) {
        Subscription subscription = subscriptionReader.read(subscriptionId);
        subscriptionManager.changeReviewStatus(subscription, reviewStatus);
    }

    public Map<Long,Boolean> getSubscriptionStatuses(Long userId, List<Long> storeIds) {
        List<Long> subscriptionIds = subscriptionReader.readByUserId(userId)
                .stream()
                .map(Subscription::getStoreId)
                .collect(Collectors.toList());

        return storeIds.stream().collect(Collectors.toMap(id -> id, subscriptionIds::contains));
    }

}
