package kr.bb.orderquery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.annotation.EnableKafka;

@EnableKafka
@EnableEurekaClient
@SpringBootApplication
public class OrderqueryApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderqueryApplication.class, args);
	}

}
