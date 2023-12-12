package kr.bb.orderquery.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "store-service")
public interface StoreFeignClient {
    @GetMapping
    String getStoreAddress(Long storeId);
}
