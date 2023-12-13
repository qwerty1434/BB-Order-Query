package kr.bb.orderquery.domain.subscription.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import kr.bb.orderquery.AbstractContainer;
import kr.bb.orderquery.domain.subscription.entity.Subscription;
import kr.bb.orderquery.domain.subscription.repository.SubscriptionRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest
class SubscriptionReaderTest extends AbstractContainer {
    @Autowired
    private SubscriptionReader subscriptionReader;
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

    @DisplayName("특정 유저의 구독 목록은 다음 결제일 기준 내림차순으로 정렬되어 있다")
    @Test
    void SubscriptionsInMypageAreSortedWithDesc() {
        // given
        Long userId = 1L;
        LocalDate now = LocalDate.now();
        Subscription s1 = createSubscriptionWithNextPaymentDate(userId, now);
        Subscription s2 = createSubscriptionWithNextPaymentDate(userId, now.plusDays(2));
        Subscription s3 = createSubscriptionWithNextPaymentDate(userId, now.minusDays(2));
        subscriptionRepository.saveAll(List.of(s1,s2,s3));

        // when
        List<Subscription> subscriptions = subscriptionReader.readByUserId(userId);

        // then
        assertThat(subscriptions).hasSize(3)
                .extracting("nextPaymentDate")
                .containsExactly(now.plusDays(2),
                        now,
                        now.minusDays(2)
                );

    }

    private Subscription createSubscriptionWithNextPaymentDate(Long userId, LocalDate nextPaymentDate){
        return Subscription.builder()
                .subscriptionId(UUID.randomUUID().toString())
                .userId(userId)
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
                .nextPaymentDate(nextPaymentDate)
                .totalOrderPrice(10_010L)
                .totalDiscountPrice(10L)
                .deliveryPrice(100L)
                .actualPrice(10_200L)
                .reviewStatus("REVIEW_READY")
                .isUnsubscribed(false)
                .build();
    }

}