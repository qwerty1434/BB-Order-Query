package kr.bb.orderquery.domain.pickup.facade;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import kr.bb.orderquery.AbstractContainer;
import kr.bb.orderquery.domain.pickup.controller.response.PickAndSubResponse;
import kr.bb.orderquery.domain.pickup.entity.Pickup;
import kr.bb.orderquery.domain.pickup.repository.PickupRepository;
import kr.bb.orderquery.domain.subscription.entity.Subscription;
import kr.bb.orderquery.domain.subscription.repository.SubscriptionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Testcontainers
@SpringBootTest
class PickupFacadeTest extends AbstractContainer {
    @Autowired
    private PickupFacade pickupFacade;
    @Autowired
    private PickupRepository pickupRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private AmazonDynamoDB amazonDynamoDb;
    @Autowired
    private DynamoDBMapper dynamoDbMapper;

    @BeforeEach
    void createTable() {
        CreateTableRequest createSubscriptionTableRequest = dynamoDbMapper
                .generateCreateTableRequest(Subscription.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        createSubscriptionTableRequest.getGlobalSecondaryIndexes().forEach(
                idx -> idx
                        .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                        .withProjection(new Projection().withProjectionType("ALL"))
        );

        TableUtils.createTableIfNotExists(amazonDynamoDb, createSubscriptionTableRequest);

        CreateTableRequest createPickupTableRequest = dynamoDbMapper
                .generateCreateTableRequest(Pickup.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        createPickupTableRequest.getGlobalSecondaryIndexes().forEach(
                idx -> idx
                        .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L))
                        .withProjection(new Projection().withProjectionType("ALL"))
        );

        TableUtils.createTableIfNotExists(amazonDynamoDb, createPickupTableRequest);

    }

    @AfterEach
    void deleteTable() {
        TableUtils.deleteTableIfExists(amazonDynamoDb, dynamoDbMapper.generateDeleteTableRequest(Subscription.class));
        TableUtils.deleteTableIfExists(amazonDynamoDb, dynamoDbMapper.generateDeleteTableRequest(Pickup.class));
    }

    @DisplayName("가게의 픽업 및 정기구독 현황을 확인할 수 있다")
    @Test
    void getDataForCalendar() {
        // given
        Long storeId = 1L;

        Subscription s1 = createSubscriptionWithStoreIdAndNextDeliveryDate(storeId, LocalDate.of(2023,12,21));
        Subscription s2 = createSubscriptionWithStoreIdAndNextDeliveryDate(storeId, LocalDate.of(2023,12,22));
        subscriptionRepository.saveAll(List.of(s1,s2));

        Pickup p1 = createPickupWithStoreIdAndPickupDate(storeId, LocalDate.of(2023,12,22));
        Pickup p2 = createPickupWithStoreIdAndPickupDate(storeId, LocalDate.of(2023,12,23));
        pickupRepository.saveAll(List.of(p1,p2));

        // when
        PickAndSubResponse dataForCalendar = pickupFacade.getDataForCalendar(storeId);

        // then
        System.out.println(dataForCalendar.getData().toString());
    }


    private Subscription createSubscriptionWithStoreIdAndNextDeliveryDate(Long storeId, LocalDate nextDeliveryDate){
        return Subscription.builder()
                .subscriptionId(UUID.randomUUID().toString())
                .userId(1L)
                .storeId(storeId)
                .subscriptionCode("구독 코드")
                .productName("장미 바구니")
                .productThumbnail("https://image_url")
                .unitPrice(1_000L)
                .quantity(10)
                .ordererName("주문자 명")
                .ordererPhoneNumber("주문자 전화번호")
                .ordererEmail("주문자 이메일")
                .recipientName("수령자 명")
                .recipientPhoneNumber("수령자 전화번호")
                .deliveryAddress("배송지")
                .paymentDateTime(LocalDateTime.now())
                .nextDeliveryDate(nextDeliveryDate.toString())
                .nextPaymentDate(LocalDate.now().plusMonths(1))
                .totalOrderPrice(10_010L)
                .totalDiscountPrice(10L)
                .deliveryPrice(100L)
                .actualPrice(10_200L)
                .reviewStatus("REVIEW_READY")
                .isUnsubscribed(false)
                .build();
    }

    private Pickup createPickupWithStoreIdAndPickupDate(Long storeId, LocalDate pickupDate) {
        return Pickup.builder()
                .pickupReservationId(UUID.randomUUID().toString())
                .reservationCode("픽업예약 코드")
                .userId(1L)
                .pickupDateTime(LocalDateTime.now())
                .pickupDate(pickupDate.toString())
                .pickupTime("00:00")
                .storeId(storeId)
                .storeAddress("가게주소")
                .productThumbnail("상품 썸네일")
                .productName("상품명")
                .unitPrice(1_000L)
                .ordererName("주문자 명")
                .ordererPhoneNumber("주문자 전화번호")
                .ordererEmail("주문자 이메일")
                .quantity(10)
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