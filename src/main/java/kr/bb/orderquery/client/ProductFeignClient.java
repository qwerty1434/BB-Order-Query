package kr.bb.orderquery.client;

import bloomingblooms.domain.product.ProductInfoDto;
import bloomingblooms.response.CommonResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "product-service")
public interface ProductFeignClient {
    @GetMapping("/client/product/{productId}")
    CommonResponse<ProductInfoDto> getProductInfo(@PathVariable(name="productId") String productId);
}
