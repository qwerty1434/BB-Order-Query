package kr.bb.orderquery.domain.pickup.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import kr.bb.orderquery.client.ProductFeignClient;
import kr.bb.orderquery.client.StoreFeignClient;
import kr.bb.orderquery.client.dto.ProductInfoDto;
import kr.bb.orderquery.domain.pickup.dto.PickupCreateDto;
import kr.bb.orderquery.domain.pickup.entity.Pickup;
import kr.bb.orderquery.domain.pickup.repository.PickupRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;

@ActiveProfiles("local")
@SpringBootTest
class PickupCreatorTest {
    @Autowired
    private PickupCreator pickupCreator;
    @Autowired
    private PickupRepository pickupRepository;
    @Autowired
    private AmazonDynamoDB amazonDynamoDb;
    @Autowired
    private DynamoDBMapper dynamoDbMapper;
    @MockBean
    private ProductFeignClient productFeignClient;
    @MockBean
    private StoreFeignClient storeFeignClient;

    @BeforeEach
    void createTable() {
        CreateTableRequest createTableRequest = dynamoDbMapper
                .generateCreateTableRequest(Pickup.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        createTableRequest.getGlobalSecondaryIndexes().forEach(
                idx -> idx
                        .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                        .withProjection(new Projection().withProjectionType("ALL"))
        );

        TableUtils.createTableIfNotExists(amazonDynamoDb, createTableRequest);
    }

    @AfterEach
    void deleteTable() {

    }

    @Test
    void createPickup(){
        // given
        String storeAddress = "서울 강남구";
        ProductInfoDto productInfoDto = createProductInfoDto();
        PickupCreateDto pickupCreateDto = createPickupCreateDto();
        Pickup pickup = pickupCreator.create(storeAddress, productInfoDto, pickupCreateDto);
        Pickup savedPickup = pickupRepository.save(pickup);

    }

    private ProductInfoDto createProductInfoDto() {
        return ProductInfoDto.builder()
                .productName("장미 바구니")
                .productThumbnail("https://image_url")
                .unitPrice(1_000L)
                .build();
    }

    private PickupCreateDto createPickupCreateDto() {
        return PickupCreateDto.builder()
                .pickupReservationId("픽업예약 아이디")
                .reservationCode("픽업예약 코드")
                .userId(1L)
                .storeId(2L)
                .productId("상품 아이디")
                .ordererName("주문자 명")
                .ordererPhoneNumber("주문자 전화번호")
                .ordererEmail("주문자 이메일")
                .quantity(10)
                .pickupDate(LocalDate.now())
                .pickupTime("13:00")
                .totalOrderPrice(10_010L)
                .totalDiscountPrice(10L)
                .deliveryPrice(100L)
                .actualPrice(10_200L)
                .paymentDateTime(LocalDateTime.now())
                .reservationStatus("RESERVATION_READY")
                .reviewStatus("REVIEW_READY")
                .cardStatus("CARD_READY")
                .build();
    }
}