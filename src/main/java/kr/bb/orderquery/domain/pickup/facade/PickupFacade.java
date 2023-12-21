package kr.bb.orderquery.domain.pickup.facade;

import bloomingblooms.domain.StatusChangeDto;
import bloomingblooms.domain.pickup.PickupCreateDto;
import bloomingblooms.domain.product.ProductInfoDto;
import bloomingblooms.domain.store.StoreNameAndAddressDto;
import kr.bb.orderquery.client.ProductFeignClient;
import kr.bb.orderquery.client.StoreFeignClient;
import kr.bb.orderquery.domain.pickup.controller.response.PickupsForDateResponse;
import kr.bb.orderquery.domain.pickup.controller.response.PickupsInMypageResponse;
import kr.bb.orderquery.domain.pickup.dto.PickupDetailDto;
import kr.bb.orderquery.domain.pickup.service.PickupService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.aws.messaging.listener.Acknowledgment;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PickupFacade {
    private final StoreFeignClient storeFeignClient;
    private final ProductFeignClient productFeignClient;
    private final PickupService pickupService;

    @KafkaListener(topics = "pickup-create", groupId = "pick-create")
    public void create(PickupCreateDto pickupCreateDto) {
        Long storeId = pickupCreateDto.getStoreId();
        StoreNameAndAddressDto storeInfo = storeFeignClient.getStoreNameAndAddress(storeId).getData();
        String productId = pickupCreateDto.getProductId();
        ProductInfoDto productInfo = productFeignClient.getProductInfo(productId).getData();
        pickupService.createPickup(storeInfo, productInfo, pickupCreateDto);
    }

    @KafkaListener(topics = "pickup-status-update", groupId = "pick-update")
    public void updateReservationStatus(StatusChangeDto statusChangeDto) {
        pickupService.updateReservationStatus(statusChangeDto.getId(), statusChangeDto.getStatus());
    }

    @SqsListener(
        value = "${cloud.aws.sqs.pickup-card-status-queue.name}",
        deletionPolicy = SqsMessageDeletionPolicy.NEVER
    )
    public void updateCardStatus(@Payload StatusChangeDto statusChangeDto, Acknowledgment ack) {
        pickupService.updateCardStatus(statusChangeDto.getId(), statusChangeDto.getStatus());
        ack.acknowledge();
    }

    @SqsListener(
        value = "${cloud.aws.sqs.pickup-review-status-queue.name}",
        deletionPolicy = SqsMessageDeletionPolicy.NEVER
    )
    public void updateReviewStatus(@Payload StatusChangeDto statusChangeDto, Acknowledgment ack) {
        pickupService.updateReviewStatus(statusChangeDto.getId(), statusChangeDto.getStatus());
        ack.acknowledge();
    }

    public PickupsInMypageResponse getPickupsOfUser(Long userId) {
        return PickupsInMypageResponse.from(pickupService.getPickupsForUser(userId));
    }

    public PickupsForDateResponse getPickupsForDate(Long storeId, String pickupDate) {
        return PickupsForDateResponse.from(pickupService.getPickupsForDate(storeId, pickupDate));
    }

    public PickupDetailDto getPickupDetail(String pickupReservationId) {
        return pickupService.getPickup(pickupReservationId);
    }
}
