package kr.bb.orderquery.domain.subscription.service;

import bloomingblooms.domain.subscription.SubscriptionCreateDto;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import kr.bb.orderquery.AbstractContainer;
import bloomingblooms.domain.product.ProductInfoDto;
import kr.bb.orderquery.domain.subscription.dto.SubscriptionDetailDto;
import kr.bb.orderquery.domain.subscription.dto.SubscriptionForDateDto;
import kr.bb.orderquery.domain.subscription.dto.SubscriptionForUserDto;
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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class SubscriptionServiceTest extends AbstractContainer {
    @MockBean
    SimpleMessageListenerContainer simpleMessageListenerContainer;
    @Autowired
    private SubscriptionService subscriptionService;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private AmazonDynamoDB amazonDynamoDb;
    @Autowired
    private DynamoDBMapper dynamoDbMapper;

    @BeforeEach
    void createTable() {
        CreateTableRequest createTableRequest = dynamoDbMapper
                .generateCreateTableRequest(Subscription.class)
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
        TableUtils.deleteTableIfExists(amazonDynamoDb, dynamoDbMapper.generateDeleteTableRequest(Subscription.class));
    }

    @DisplayName("구독 데이터를 저장한다")
    @Test
    void createSubscription() {
        // given
        String subscriptionId = UUID.randomUUID().toString();
        SubscriptionCreateDto subscriptionCreateDto = createSubscriptionCreateDto(subscriptionId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

        // when
        subscriptionService.createSubscription(subscriptionCreateDto);

        // then
        Subscription result = subscriptionRepository.findById(subscriptionId).get();

        assertThat(result).isNotNull()
                .extracting("subscriptionId", "userId", "paymentDateTime" ,"isUnsubscribed")
                .containsExactly(subscriptionId,
                        subscriptionCreateDto.getUserId(),
                        LocalDateTime.parse(subscriptionCreateDto.getPaymentDateTime().format(formatter),formatter),
                        false
                );
    }

    @DisplayName("특정 유저의 구독 목록만 가져온다")
    @Test
    void getSubscriptionsOfUser() {
        // given
        Long userId = 1L;
        Subscription s1 = createSubscriptionWithUserId(userId);
        Subscription s2 = createSubscriptionWithUserId(userId);
        s2.setIsUnsubscribed(true);
        Subscription s3 = createSubscriptionWithUserId(2L);
        Subscription s4 = createSubscriptionWithUserId(3L);
        subscriptionRepository.saveAll(List.of(s1,s2,s3,s4));

        // when
        List<SubscriptionForUserDto> subscriptionsOfUser = subscriptionService.getSubscriptionsOfUser(userId);

        // then
        assertThat(subscriptionsOfUser).hasSize(1);
    }

    @DisplayName("특정 가게의 특정 날짜에 예정되어 있는 구독 목록을 가져온다")
    @Test
    void getSubscriptionsForDate() {
        // given
        Long storeId = 2L;
        LocalDate nextPaymentDate = LocalDate.now();

        Subscription s1 = createSubscriptionWithStoreIdAndNextDeliveryDate(storeId, nextPaymentDate);
        Subscription s2 = createSubscriptionWithStoreIdAndNextDeliveryDate(storeId, nextPaymentDate);
        Subscription s3 = createSubscriptionWithStoreIdAndNextDeliveryDate(storeId, nextPaymentDate.plusDays(5));
        Subscription s4 = createSubscriptionWithStoreIdAndNextDeliveryDate(1L, nextPaymentDate); // 다른 가게
        subscriptionRepository.saveAll(List.of(s1,s2,s3,s4));

        // when
        List<SubscriptionForDateDto> subscriptionsForDate = subscriptionService.getSubscriptionsForDate(storeId, nextPaymentDate.toString());

        // then
        assertThat(subscriptionsForDate).hasSize(2);
    }

    @DisplayName("구독 아이디를 통해 특정 구독정보를 가져온다")
    @Test
    void getSubscriptionDetail() {
        // given
        String subscriptionId = UUID.randomUUID().toString();
        Subscription subscription = createSubscriptionWithId(subscriptionId);
        subscriptionRepository.save(subscription);

        // when
        SubscriptionDetailDto subscriptionDetailDto = subscriptionService.getSubscription(subscriptionId);

        // then
        assertThat(subscriptionDetailDto.getNextDeliveryDate()).isEqualTo(subscription.getNextDeliveryDate());

    }

    @DisplayName("해당 유저의 가게 구독여부를 반환한다")
    @Test
    void getSubscriptionStatuses() {
        // given
        Long userId = 1L;

        Subscription s1 = createSubscriptionWithUserIdAndStoreId(userId, 1L);
        Subscription s2 = createSubscriptionWithUserIdAndStoreId(userId, 2L);
        subscriptionRepository.saveAll(List.of(s1,s2));

        List<Long> storeIds = List.of(1L,2L,3L);

        // when
        Map<Long, Boolean> result = subscriptionService.getSubscriptionStatuses(userId, storeIds);

        // then
        assertThat(result.get(1L)).isTrue();
        assertThat(result.get(2L)).isTrue();
        assertThat(result.get(3L)).isFalse();
    }





    private SubscriptionCreateDto createSubscriptionCreateDto(String subscriptionId) {
        return SubscriptionCreateDto.builder()
                .subscriptionId(subscriptionId)
                .userId(1L)
                .storeId(2L)
                .productId("상품 아이디")
                .quantity(10)
                .ordererName("주문자 명")
                .ordererPhoneNumber("주문자 전화번호")
                .ordererEmail("주문자 이메일")
                .recipientName("수령자 명")
                .recipientPhoneNumber("수령자 전화번호")
                .storeName("가게이름")
                .zipcode("우편번호")
                .roadName("도로명주소")
                .addressDetail("상세주소")
                .paymentDateTime(LocalDateTime.now())
                .nextDeliveryDate(LocalDate.now().plusMonths(1))
                .nextPaymentDate(LocalDate.now().plusMonths(1))
                .totalOrderPrice(10_010L)
                .totalDiscountPrice(10L)
                .deliveryPrice(100L)
                .actualPrice(10_200L)
                .reviewStatus("REVIEW_READY")
                .deliveryRequest("요청사항")
                .productName("장미 바구니")
                .productThumbnail("https://image_url")
                .unitPrice(1_000L)
                .storeId(1L)
                .build();
    }

    private Subscription createSubscriptionWithUserId(Long userId){
        return Subscription.builder()
                .subscriptionId(UUID.randomUUID().toString())
                .userId(userId)
                .storeId(1L)
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
                .nextDeliveryDate(LocalDate.now().plusMonths(1).toString())
                .nextPaymentDate(LocalDate.now().plusMonths(1))
                .totalOrderPrice(10_010L)
                .totalDiscountPrice(10L)
                .deliveryPrice(100L)
                .actualPrice(10_200L)
                .reviewStatus("REVIEW_READY")
                .isUnsubscribed(false)
                .build();
    }
    private Subscription createSubscriptionWithUserIdAndStoreId(Long userId, Long storeId){
        return Subscription.builder()
                .subscriptionId(UUID.randomUUID().toString())
                .userId(userId)
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
                .nextDeliveryDate(LocalDate.now().plusMonths(1).toString())
                .nextPaymentDate(LocalDate.now().plusMonths(1))
                .totalOrderPrice(10_010L)
                .totalDiscountPrice(10L)
                .deliveryPrice(100L)
                .actualPrice(10_200L)
                .reviewStatus("REVIEW_READY")
                .isUnsubscribed(false)
                .build();
    }

    private Subscription createSubscriptionWithId(String subscriptionId){
        return Subscription.builder()
                .subscriptionId(subscriptionId)
                .userId(1L)
                .storeId(1L)
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
                .nextDeliveryDate(LocalDate.now().plusMonths(1).toString())
                .nextPaymentDate(LocalDate.now().plusMonths(1))
                .totalOrderPrice(10_010L)
                .totalDiscountPrice(10L)
                .deliveryPrice(100L)
                .actualPrice(10_200L)
                .reviewStatus("REVIEW_READY")
                .isUnsubscribed(false)
                .build();
    }

    private Subscription createSubscriptionWithStoreIdAndNextDeliveryDate(Long storeId, LocalDate nextDeliveryDate) {
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

}