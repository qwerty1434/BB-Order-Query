package kr.bb.orderquery.client;

import bloomingblooms.domain.store.StoreNameAndAddressDto;
import bloomingblooms.response.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "store-service")
public interface StoreFeignClient {

    @GetMapping("/client/stores/{storeId}/info")
    CommonResponse<StoreNameAndAddressDto> getStoreNameAndAddress(@PathVariable(name = "storeId") Long storeId);
}
