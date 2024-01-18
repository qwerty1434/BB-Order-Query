package kr.bb.orderquery.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;

@EnableFeignClients(basePackages = "kr.bb.orderquery")
@Configuration
public class FeignConfig {
}
