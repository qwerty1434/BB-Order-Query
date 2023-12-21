package kr.bb.orderquery.client;

import bloomingblooms.response.CommonResponse;
import kr.bb.orderquery.client.dto.StoreInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "store-service")
public interface StoreFeignClient {
    @GetMapping
    CommonResponse<String> getStoreAddress(Long storeId);

    @GetMapping("/client/stores/{storeId}/info")
    CommonResponse<StoreInfoDto> getStoreInfo(@PathVariable(name = "storeId") Long storeId);
}
