package kr.bb.orderquery.client;

import kr.bb.orderquery.client.dto.StoreInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "store-service")
public interface StoreFeignClient {
    @GetMapping
    String getStoreAddress(Long storeId);

    @GetMapping("/stores/{storeId}/info")
    StoreInfoDto getStoreInfo(@PathVariable Long storeId);
}
