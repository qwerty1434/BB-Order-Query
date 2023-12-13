package kr.bb.orderquery.client;

import kr.bb.orderquery.client.dto.StoreInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "store-service")
public interface StoreFeignClient {
    @GetMapping
    String getStoreAddress(Long storeId);

    @GetMapping
    StoreInfoDto getStoreInfo(Long storeId);
}
