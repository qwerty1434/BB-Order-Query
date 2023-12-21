package kr.bb.orderquery.client;

import bloomingblooms.response.CommonResponse;
import kr.bb.orderquery.client.dto.ProductInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "product-service")
public interface ProductFeignClient {
    @GetMapping
    CommonResponse<ProductInfoDto> getProductInfo(String productId);
}
