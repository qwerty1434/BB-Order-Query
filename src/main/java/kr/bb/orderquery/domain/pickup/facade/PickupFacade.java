package kr.bb.orderquery.domain.pickup.facade;

import bloomingblooms.domain.StatusChangeDto;
import bloomingblooms.domain.pickup.PickupCreateDto;
import bloomingblooms.domain.store.StoreNameAndAddressDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.bb.orderquery.client.StoreFeignClient;
import kr.bb.orderquery.domain.pickup.controller.response.PickAndSubResponse;
import kr.bb.orderquery.domain.pickup.controller.response.PickupsForDateResponse;
import kr.bb.orderquery.domain.pickup.controller.response.PickupsInMypageResponse;
import kr.bb.orderquery.domain.pickup.dto.PickupDetailDto;
import kr.bb.orderquery.domain.pickup.entity.Pickup;
import kr.bb.orderquery.domain.pickup.service.PickupService;
import kr.bb.orderquery.domain.subscription.entity.Subscription;
import kr.bb.orderquery.domain.subscription.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class PickupFacade {
    private final PickupService pickupService;
    private final SubscriptionService subscriptionService;
    private final StoreFeignClient storeFeignClient;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "pickup-create", groupId = "pick-create")
    public void create(PickupCreateDto pickupCreateDto) {
        Long storeId = pickupCreateDto.getStoreId();
        StoreNameAndAddressDto storeInfo = storeFeignClient.getStoreNameAndAddress(storeId).getData();
        pickupService.createPickup(storeInfo, pickupCreateDto);
    }

    @KafkaListener(topics = "pickup-status-update", groupId = "pick-update")
    public void updateReservationStatus(StatusChangeDto statusChangeDto) {
        pickupService.updateReservationStatus(statusChangeDto.getId(), statusChangeDto.getStatus());
    }

    @SqsListener(
        value = "${cloud.aws.sqs.pickup-card-status-queue.name}",
        deletionPolicy = SqsMessageDeletionPolicy.NEVER
    )
    public void updateCardStatus(@Payload String message, Acknowledgment ack) throws JsonProcessingException {
        StatusChangeDto statusChangeDto = objectMapper.readValue(message, StatusChangeDto.class);
        pickupService.updateCardStatus(statusChangeDto.getId(), statusChangeDto.getStatus());
        ack.acknowledge();
    }

    @SqsListener(
        value = "${cloud.aws.sqs.pickup-review-status-queue.name}",
        deletionPolicy = SqsMessageDeletionPolicy.NEVER
    )
    public void updateReviewStatus(@Payload String message, Acknowledgment ack) throws JsonProcessingException {
        StatusChangeDto statusChangeDto = objectMapper.readValue(message, StatusChangeDto.class);
        pickupService.updateReviewStatus(statusChangeDto.getId(), statusChangeDto.getStatus());
        ack.acknowledge();
    }

    public PickupsInMypageResponse getPickupsOfUser(Long userId, Pageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        return PickupsInMypageResponse.from(pickupService.getPickupsForUser(userId, pageable, now));
    }

    public PickupsForDateResponse getPickupsForDate(Long storeId, String pickupDate) {
        return PickupsForDateResponse.from(pickupService.getPickupsForDate(storeId, pickupDate));
    }

    public PickupDetailDto getPickupDetail(String pickupReservationId) {
        return pickupService.getPickup(pickupReservationId);
    }

    public PickAndSubResponse getDataForCalendar(Long storeId) {
        List<Pickup> pickups = pickupService.getPickupByStoreId(storeId);
        List<Subscription> subscriptions = subscriptionService.getSubscriptionByStoreId(storeId);
        return PickAndSubResponse.of(pickups,subscriptions);
    }

}
