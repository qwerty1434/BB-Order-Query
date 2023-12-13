package kr.bb.orderquery.domain.pickup.facade;

import kr.bb.orderquery.client.ProductFeignClient;
import kr.bb.orderquery.client.StoreFeignClient;
import kr.bb.orderquery.client.dto.ProductInfoDto;
import kr.bb.orderquery.client.dto.StatusChangeDto;
import kr.bb.orderquery.domain.pickup.controller.response.PickupsForDateResponse;
import kr.bb.orderquery.domain.pickup.controller.response.PickupsInMypageResponse;
import kr.bb.orderquery.domain.pickup.dto.PickupCreateDto;
import kr.bb.orderquery.domain.pickup.dto.PickupDetailDto;
import kr.bb.orderquery.domain.pickup.service.PickupService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PickupFacade {
    private final StoreFeignClient storeFeignClient;
    private final ProductFeignClient productFeignClient;
    private final PickupService pickupService;

//    @KafkaListener
    public void create(PickupCreateDto pickupCreateDto) {
        Long storeId = pickupCreateDto.getStoreId();
        String storeAddress = storeFeignClient.getStoreAddress(storeId);
        String productId = pickupCreateDto.getProductId();
        ProductInfoDto productInfo = productFeignClient.getProductInfo(productId);
        pickupService.createPickup(storeAddress, productInfo, pickupCreateDto);
    }

    //    @KafkaListener
    public void updateReservationStatus(StatusChangeDto statusChangeDto) {
        pickupService.updateReservationStatus(statusChangeDto.getId(), statusChangeDto.getStatus());
    }

    //    @SqsListener()
    public void updateCardStatus(@Payload StatusChangeDto statusChangeDto) {
        pickupService.updateCardStatus(statusChangeDto.getId(), statusChangeDto.getStatus());
    }

    //    @SqsListener()
    public void updateReviewStatus(@Payload StatusChangeDto statusChangeDto) {
        pickupService.updateReviewStatus(statusChangeDto.getId(), statusChangeDto.getStatus());
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
