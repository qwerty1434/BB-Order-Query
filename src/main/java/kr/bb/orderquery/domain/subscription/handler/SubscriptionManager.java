package kr.bb.orderquery.domain.subscription.handler;

import kr.bb.orderquery.domain.subscription.entity.Subscription;
import kr.bb.orderquery.domain.subscription.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class SubscriptionManager {
    private final SubscriptionRepository subscriptionRepository;

    public void updateNextDeliveryDate(Subscription subscription, LocalDate nextDeliveryDate, LocalDate nextPaymentDate) {
        subscription.setNextDeliveryDate(nextDeliveryDate.toString());
        subscription.setNextPaymentDate(nextPaymentDate);
        subscriptionRepository.save(subscription);
    }

    public void changeSubscriptionStatus(Subscription subscription, String subscriptionStatus, String reviewStatus) {
        subscription.updateStatus(subscriptionStatus, reviewStatus);
        subscriptionRepository.save(subscription);
    }

    public void changeReviewStatus(Subscription subscription, String reviewStatus) {
        subscription.setReviewStatus(reviewStatus);
        subscriptionRepository.save(subscription);
    }

}
