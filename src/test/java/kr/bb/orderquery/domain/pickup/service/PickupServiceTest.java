package kr.bb.orderquery.domain.pickup.service;

import bloomingblooms.domain.pickup.PickupCreateDto;
import bloomingblooms.domain.store.StoreNameAndAddressDto;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.Projection;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import kr.bb.orderquery.AbstractContainer;
import bloomingblooms.domain.product.ProductInfoDto;
import kr.bb.orderquery.domain.pickup.dto.PickupDetailDto;
import kr.bb.orderquery.domain.pickup.dto.PickupsForDateDto;
import kr.bb.orderquery.domain.pickup.dto.PickupsInMypageDto;
import kr.bb.orderquery.domain.pickup.entity.Pickup;
import kr.bb.orderquery.domain.pickup.repository.PickupRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("local")
@SpringBootTest
class PickupServiceTest extends AbstractContainer {
    @Autowired
    private PickupService pickupService;
    @Autowired
    private PickupRepository pickupRepository;
    @Autowired
    private AmazonDynamoDB amazonDynamoDb;
    @Autowired
    private DynamoDBMapper dynamoDbMapper;

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
        TableUtils.deleteTableIfExists(amazonDynamoDb, dynamoDbMapper.generateDeleteTableRequest(Pickup.class));
    }


    @DisplayName("픽업예약 데이터를 저장한다")
    @Test
    void createPickup(){
        // given
        StoreNameAndAddressDto storeInfo = StoreNameAndAddressDto.builder()
                .storeName("가게명")
                .storeAddress("가게주소")
                .build();
        String pickupReservationId = UUID.randomUUID().toString();
        PickupCreateDto pickupCreateDto = createPickupCreateDto(pickupReservationId);

        // when
        Pickup pickup = pickupService.createPickup(storeInfo, pickupCreateDto);

        // then
        Pickup result = pickupRepository.findById(pickup.getPickupReservationId()).get();

        assertThat(result).isNotNull()
                .extracting("pickupReservationId",
                        "pickupDateTime",
                        "pickupDate",
                        "userId",
                        "productName"
                )
                .containsExactlyInAnyOrder(pickupReservationId,
                        combineDateAndTime(pickupCreateDto.getPickupDate(), pickupCreateDto.getPickupTime()),
                        pickupCreateDto.getPickupDate().toString(),
                        pickupCreateDto.getUserId(),
                        pickupCreateDto.getProductName()
                );

    }

    @DisplayName("특정 유저의 픽업예약 목록만 가져온다")
    @Test
    void getPickupsOfUser() {
        // given
        Long userId = 1L;
        Pickup p1 = createPickupWithUserId(userId);
        Pickup p2 = createPickupWithUserId(userId);
        Pickup p3 = createPickupWithUserId(2L);
        Pickup p4 = createPickupWithUserId(3L);
        pickupRepository.saveAll(List.of(p1,p2,p3,p4));

        // when
        List<PickupsInMypageDto> pickupsForUser = pickupService.getPickupsForUser(userId);

        // then
        assertThat(pickupsForUser).hasSize(2);

    }

    @DisplayName("특정 유저의 픽업예약 목록은 픽업일 기준 내림차순으로 정렬되어 있다")
    @Test
    void pickupsInMypageAreSortedWithDesc() {
        // given
        Long userId = 1L;
        LocalDateTime now = LocalDateTime.of(LocalDate.of(2023,12,31), LocalTime.now());
        Pickup p1 = createPickupWithPickupDate(userId, now);
        Pickup p2 = createPickupWithPickupDate(userId, now.plusDays(2));
        Pickup p3 = createPickupWithPickupDate(userId, now.minusDays(2));
        pickupRepository.saveAll(List.of(p1,p2,p3));

        // when
        List<PickupsInMypageDto> pickupsForUser = pickupService.getPickupsForUser(userId);

        // then
        assertThat(pickupsForUser).hasSize(3)
                .extracting("pickupDate")
                .containsExactly(now.toLocalDate().plusDays(2).toString(),
                        now.toLocalDate().toString(),
                        now.toLocalDate().minusDays(2).toString()
                );

    }

    @DisplayName("픽업예약 목록은 예약 시간 기준 내림차순으로 정렬되어 있다")
    @Test
    void pickupsForUserAreSortedWithPickupTimeDesc() {
        // given
        Long userId = 1L;

        Pickup p1 = createPickupWithPickupTime(userId, "12:30");
        Pickup p2 = createPickupWithPickupTime(userId, "13:00");
        Pickup p3 = createPickupWithPickupTime(userId, "12:00");
        pickupRepository.saveAll(List.of(p1,p2,p3));

        // when
        List<PickupsInMypageDto> pickupsForUser = pickupService.getPickupsForUser(userId);

        // then
        assertThat(pickupsForUser).hasSize(3)
                .extracting("pickupTime")
                .containsExactly("13:00","12:30","12:00");
    }

    @DisplayName("특정 가게의 특정 날짜에 예정되어 있는 픽업예약 목록을 가져온다")
    @Test
    void getPickupsForDate() {
        // given
        LocalDate pickupDate = LocalDate.now();
        Long storeId = 2L;

        Pickup p1 = createPickupWithStoreIdAndPickupDate(storeId,pickupDate);
        Pickup p2 = createPickupWithStoreIdAndPickupDate(storeId,pickupDate);
        Pickup p3 = createPickupWithStoreIdAndPickupDate(storeId,pickupDate.plusDays(5)); // 다른 날짜
        Pickup p4 = createPickupWithStoreIdAndPickupDate(1L,pickupDate); // 다른 가게

        pickupRepository.saveAll(List.of(p1,p2,p3,p4));

        // when
        List<PickupsForDateDto> pickupsForDate = pickupService.getPickupsForDate(storeId, pickupDate.toString());

        // then
        assertThat(pickupsForDate).hasSize(2);

    }

    @DisplayName("픽업예약 아이디를 통해 픽업예약 정보를 가져온다")
    @Test
    void getPickupReservationDetail() {
        // given
        String pickupId = UUID.randomUUID().toString();
        Pickup pickup = createPickupWithId(pickupId);
        pickupRepository.save(pickup);

        // when
        PickupDetailDto pickupDetailDto = pickupService.getPickup(pickupId);

        // then
        assertThat(pickupDetailDto.getPickupDate()).isEqualTo(pickup.getPickupDate());
    }


    private PickupCreateDto createPickupCreateDto(String pickupReservationId) {
        return PickupCreateDto.builder()
                .pickupReservationId(pickupReservationId)
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
                .actualPrice(10_200L)
                .paymentDateTime(LocalDateTime.now())
                .reservationStatus("RESERVATION_READY")
                .reviewStatus("REVIEW_READY")
                .cardStatus("CARD_READY")
                .productName("장미 바구니")
                .productThumbnail("https://image_url")
                .unitPrice(1_000L)
                .build();
    }

    private Pickup createPickupWithUserId(Long userId) {
        return Pickup.builder()
                .pickupReservationId(UUID.randomUUID().toString())
                .reservationCode("픽업예약 코드")
                .userId(userId)
                .pickupDateTime(LocalDateTime.now())
                .pickupDate(LocalDate.now().toString())
                .pickupTime("00:00")
                .storeId(2L)
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
    private Pickup createPickupWithId(String pickupId) {
        return Pickup.builder()
                .pickupReservationId(pickupId)
                .reservationCode("픽업예약 코드")
                .userId(1L)
                .pickupDateTime(LocalDateTime.now())
                .pickupDate(LocalDate.now().toString())
                .pickupTime("00:00")
                .storeId(2L)
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

    private Pickup createPickupWithPickupDate(Long userId, LocalDateTime pickupDateTime) {
        return Pickup.builder()
                .pickupReservationId(UUID.randomUUID().toString())
                .reservationCode("픽업예약 코드")
                .userId(userId)
                .pickupDateTime(pickupDateTime)
                .pickupDate(pickupDateTime.toLocalDate().toString())
                .pickupTime(pickupDateTime.toLocalTime().toString().formatted("HH:mm"))
                .storeId(2L)
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
    private Pickup createPickupWithPickupTime(Long userId, String pickupTime) {
        return Pickup.builder()
                .pickupReservationId(UUID.randomUUID().toString())
                .reservationCode("픽업예약 코드")
                .userId(userId)
                .pickupDateTime(LocalDateTime.of(LocalDate.now(),LocalTime.parse(pickupTime,DateTimeFormatter.ofPattern("HH:mm"))))
                .pickupDate(LocalDate.now().toString())
                .pickupTime(pickupTime)
                .storeId(2L)
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

    private LocalDateTime combineDateAndTime(LocalDate date, String time) {
        LocalTime localTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        return LocalDateTime.of(date,localTime);
    }


}