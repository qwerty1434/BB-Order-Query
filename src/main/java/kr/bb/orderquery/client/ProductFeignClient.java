package kr.bb.orderquery.client;

import kr.bb.orderquery.client.dto.ProductInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "product-service")
public interface ProductFeignClient {
    @GetMapping
    ProductInfoDto getProductInfo(String productId);
}
