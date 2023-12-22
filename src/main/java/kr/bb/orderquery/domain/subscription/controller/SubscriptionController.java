package kr.bb.orderquery.domain.subscription.controller;

import bloomingblooms.response.CommonResponse;
import kr.bb.orderquery.domain.subscription.controller.response.SubscriptionsForDateResponse;
import kr.bb.orderquery.domain.subscription.controller.response.SubscriptionsForMypageResponse;
import kr.bb.orderquery.domain.subscription.dto.SubscriptionDetailDto;
import kr.bb.orderquery.domain.subscription.facade.SubscriptionFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class SubscriptionController {
    private final SubscriptionFacade subscriptionFacade;

    @GetMapping("/store-subscriptions")
    public CommonResponse<SubscriptionsForMypageResponse> subscriptionsForMypage(@RequestHeader(value = "userId") Long userId) {
        return CommonResponse.success(subscriptionFacade.getSubscriptionsOfUser(userId));
    }

    @GetMapping("/{storeId}/store-subscriptions")
    public CommonResponse<SubscriptionsForDateResponse> subscriptionsForDate(@PathVariable Long storeId,
                                                                             @RequestParam String year,
                                                                             @RequestParam String month,
                                                                             @RequestParam String day) {
        LocalDate nextDeliveryDate = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        return CommonResponse.success(subscriptionFacade.getSubscriptionsForDate(storeId, nextDeliveryDate.toString()));
    }

    @GetMapping("/store-subscriptions/{subscriptionId}")
    public CommonResponse<SubscriptionDetailDto> subscriptionDetail(@PathVariable String subscriptionId) {
        return CommonResponse.success(subscriptionFacade.getSubscriptionDetail(subscriptionId));
    }


}
