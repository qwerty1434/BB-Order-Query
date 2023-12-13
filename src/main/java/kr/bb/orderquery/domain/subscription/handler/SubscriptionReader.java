package kr.bb.orderquery.domain.subscription.handler;

import kr.bb.orderquery.domain.subscription.entity.Subscription;
import kr.bb.orderquery.domain.subscription.exception.SubscriptionNotFoundException;
import kr.bb.orderquery.domain.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SubscriptionReader {
    private final SubscriptionRepository subscriptionRepository;

    public List<Subscription> readByUserId(Long userId) {
        return subscriptionRepository.findAllByUserIdOrderByNextPaymentDateDesc(userId);
    }

    public List<Subscription> readByStoreIdAndNextDeliveryDate(Long storeId, String nextDeliveryDate) {
        return subscriptionRepository.findAllByStoreIdAndNextDeliveryDate(storeId, nextDeliveryDate);
    }

    public Subscription read(String subscriptionId) {
        return subscriptionRepository.findById(subscriptionId).orElseThrow(SubscriptionNotFoundException::new);
    }
}
