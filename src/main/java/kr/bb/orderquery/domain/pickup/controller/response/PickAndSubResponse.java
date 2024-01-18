package kr.bb.orderquery.domain.pickup.controller.response;

import kr.bb.orderquery.domain.pickup.entity.Pickup;
import kr.bb.orderquery.domain.subscription.entity.Subscription;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickAndSubResponse {
    List<String> data;

    public static PickAndSubResponse of(List<Pickup> pickups, List<Subscription> subscriptions) {
        Set<String> pickupSchedule = makePickupSchedule(pickups);
        Set<String> subscriptionSchedule = makeSubscriptionSchedule(subscriptions);
        List<String> schedules = concatPickupAndSubscriptionSchedules(pickupSchedule, subscriptionSchedule);
        Collections.sort(schedules);
        return PickAndSubResponse.builder()
                .data(schedules)
                .build();
    }

    private static Set<String> makePickupSchedule(List<Pickup> pickups) {
        return pickups.stream()
                .map(pickup -> pickup.getPickupDate() + " PICKUP")
                .collect(Collectors.toSet());
    }

    private static Set<String> makeSubscriptionSchedule(List<Subscription> subscriptions) {
        return subscriptions.stream()
                .map(subscription -> subscription.getNextDeliveryDate() + " SUBSCRIPTION")
                .collect(Collectors.toSet());
    }

    private static List<String> concatPickupAndSubscriptionSchedules(Set<String> pickupSchedule, Set<String> subscriptionSchedule) {
        return Stream.concat(pickupSchedule.stream(), subscriptionSchedule.stream())
                .collect(Collectors.toList());
    }


}
