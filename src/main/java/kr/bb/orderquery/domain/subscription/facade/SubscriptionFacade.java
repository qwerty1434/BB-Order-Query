package kr.bb.orderquery.domain.subscription.facade;

import bloomingblooms.domain.StatusChangeDto;
import bloomingblooms.domain.subscription.SubscriptionCreateDto;
import bloomingblooms.domain.subscription.SubscriptionDateDto;
import kr.bb.orderquery.client.ProductFeignClient;
import bloomingblooms.domain.product.ProductInfoDto;
import kr.bb.orderquery.domain.subscription.controller.response.SubscriptionsForDateResponse;
import kr.bb.orderquery.domain.subscription.controller.response.SubscriptionsForMypageResponse;
import kr.bb.orderquery.domain.subscription.dto.SubscriptionDetailDto;
import kr.bb.orderquery.domain.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SubscriptionFacade {
    private final SubscriptionService subscriptionService;
    private final ProductFeignClient productFeignClient;

    @KafkaListener(topics = "subscription-create", groupId = "sub-create")
    public void create(SubscriptionCreateDto subscriptionCreateDto) {
        subscriptionService.createSubscription(subscriptionCreateDto);
    }

    @KafkaListener(topics = "subscription-date-update", groupId = "sub-update")
    public void updateSubscriptionDate(SubscriptionDateDto subscriptionDateDto) {
        subscriptionService.updateSubscriptionDate(subscriptionDateDto.getSubscriptionId(),
                subscriptionDateDto.getNextDeliveryDate(), subscriptionDateDto.getNextPaymentDate());
    }

    @KafkaListener(topics= "unsubscribe", groupId = "unsub")
    public void unSubscribe(String subscriptionId) {
        subscriptionService.unSubscribe(subscriptionId);
    }

    @SqsListener(
        value = "${cloud.aws.sqs.subscription-review-status-queue.name}",
        deletionPolicy = SqsMessageDeletionPolicy.NEVER
    )
    public void updateReviewStatus(@Payload StatusChangeDto statusChangeDto, Acknowledgment ack) {
        subscriptionService.updateReviewStatus(statusChangeDto.getId(), statusChangeDto.getStatus());
        ack.acknowledge();
    }

    public SubscriptionsForMypageResponse getSubscriptionsOfUser(Long userId) {
        return SubscriptionsForMypageResponse.from(subscriptionService.getSubscriptionsOfUser(userId));
    }

    public SubscriptionsForDateResponse getSubscriptionsForDate(Long storeId, String nextDeliveryDate) {
        return SubscriptionsForDateResponse.from(subscriptionService.getSubscriptionsForDate(storeId, nextDeliveryDate));
    }

    public SubscriptionDetailDto getSubscriptionDetail(String subscriptionId) {
        return subscriptionService.getSubscription(subscriptionId);
    }

    public Map<Long,Boolean> getSubscriptionStatuses(Long userId, List<Long> storeIds) {
        return subscriptionService.getSubscriptionStatuses(userId, storeIds);
    }

}
