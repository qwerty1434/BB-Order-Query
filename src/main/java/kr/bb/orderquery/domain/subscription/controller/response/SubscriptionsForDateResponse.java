package kr.bb.orderquery.domain.subscription.controller.response;

import kr.bb.orderquery.domain.subscription.dto.SubscriptionForDateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionsForDateResponse {
    private List<SubscriptionForDateDto> data;

    public static SubscriptionsForDateResponse from(List<SubscriptionForDateDto> data) {
        return SubscriptionsForDateResponse.builder()
                .data(data)
                .build();
    }
}
