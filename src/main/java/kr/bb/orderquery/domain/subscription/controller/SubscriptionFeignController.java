package kr.bb.orderquery.domain.subscription.controller;

import bloomingblooms.response.CommonResponse;
import kr.bb.orderquery.domain.subscription.facade.SubscriptionFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/client/order-query/subs")
public class SubscriptionFeignController {
    private final SubscriptionFacade subscriptionFacade;

    @PostMapping("/lists")
    public CommonResponse<Map<Long,Boolean>> getSubscriptions(
            @RequestHeader(value = "userId") Long userId, @RequestBody List<Long> storeIds) {
        return CommonResponse.success(subscriptionFacade.getSubscriptionStatuses(userId, storeIds));
    }
}
