package kr.bb.orderquery.domain.pickup.facade;

import kr.bb.orderquery.client.ProductFeignClient;
import kr.bb.orderquery.client.StoreFeignClient;
import kr.bb.orderquery.client.dto.ProductInfoDto;
import kr.bb.orderquery.domain.pickup.dto.PickupCreateDto;
import kr.bb.orderquery.domain.pickup.dto.PickupDetailDto;
import kr.bb.orderquery.domain.pickup.dto.PickupsForDateDto;
import kr.bb.orderquery.domain.pickup.dto.PickupsInMypageDto;
import kr.bb.orderquery.domain.pickup.service.PickupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public List<PickupsInMypageDto> getPickupsForUser(Long userId) {
        return pickupService.getPickupsForUser(userId);
    }

    public List<PickupsForDateDto> getPickupsForDate(Long storeId, String pickupDate) {
        return pickupService.getPickupsForDate(storeId, pickupDate);
    }

    public PickupDetailDto getPickupDetail(String pickupReservationId) {
        return pickupService.getPickup(pickupReservationId);
    }
}
