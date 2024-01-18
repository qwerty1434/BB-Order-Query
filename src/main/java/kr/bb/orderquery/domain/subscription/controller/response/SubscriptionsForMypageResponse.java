package kr.bb.orderquery.domain.subscription.controller.response;

import kr.bb.orderquery.domain.subscription.dto.SubscriptionForUserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionsForMypageResponse {
    private List<SubscriptionForUserDto> data;

    public static SubscriptionsForMypageResponse from(List<SubscriptionForUserDto> data) {
        return SubscriptionsForMypageResponse.builder()
                .data(data)
                .build();
    }
}
