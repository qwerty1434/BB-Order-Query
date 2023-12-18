package kr.bb.orderquery.domain.subscription.facade;

import bloomingblooms.domain.subscription.SubscriptionCreateDto;
import kr.bb.orderquery.client.ProductFeignClient;
import kr.bb.orderquery.client.dto.ProductInfoDto;
import kr.bb.orderquery.domain.subscription.controller.response.SubscriptionsForDateResponse;
import kr.bb.orderquery.domain.subscription.controller.response.SubscriptionsForMypageResponse;
import kr.bb.orderquery.domain.subscription.dto.SubscriptionDetailDto;
import kr.bb.orderquery.domain.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubscriptionFacade {
    private final SubscriptionService subscriptionService;
    private final ProductFeignClient productFeignClient;


//    @KafkaListener
    public void create(SubscriptionCreateDto subscriptionCreateDto) {
        String productId = subscriptionCreateDto.getProductId();
        ProductInfoDto productInfoDto = productFeignClient.getProductInfo(productId);
        subscriptionService.createSubscription(productInfoDto, subscriptionCreateDto);
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


}
