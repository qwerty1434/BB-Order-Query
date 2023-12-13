package kr.bb.orderquery.domain.pickup.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import kr.bb.orderquery.config.DynamoDbConfig;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName="PickupReservation")
public class Pickup {
    @Id
    @DynamoDBHashKey(attributeName = "pickup_reservation_id")
    private String pickupReservationId;

    @DynamoDBIndexRangeKey(attributeName = "pickup_date_time", globalSecondaryIndexNames = {"byPickupDate","byUserId"})
    @DynamoDBTypeConverted(converter = DynamoDbConfig.LocalDateTimeConverter.class)
    private LocalDateTime pickupDateTime;

    @DynamoDBIndexHashKey(attributeName = "pickup_date", globalSecondaryIndexName = "byPickupDate")
    private String pickupDate;

    @DynamoDBIndexHashKey(attributeName = "user_id", globalSecondaryIndexName = "byUserId")
    private Long userId;

    @DynamoDBAttribute(attributeName = "pickup_time")
    private String pickupTime;

    @DynamoDBAttribute(attributeName = "reservation_code")
    private String reservationCode;

    @DynamoDBIndexHashKey(attributeName = "store_id", globalSecondaryIndexName = "byStoreId")
    private Long storeId;

    @DynamoDBAttribute(attributeName = "store_name")
    private String storeName;

    @DynamoDBAttribute(attributeName = "store_address")
    private String storeAddress;

    @DynamoDBAttribute(attributeName = "product_thumbnail")
    private String productThumbnail;

    @DynamoDBAttribute(attributeName = "product_name")
    private String productName;

    @DynamoDBAttribute(attributeName = "unit_price")
    private Long unitPrice;

    @DynamoDBAttribute(attributeName = "orderer_name")
    private String ordererName;

    @DynamoDBAttribute(attributeName = "orderer_phone_number")
    private String ordererPhoneNumber;

    @DynamoDBAttribute(attributeName = "orderer_email")
    private String ordererEmail;

    @DynamoDBAttribute(attributeName = "quantity")
    private Integer quantity;

    @DynamoDBAttribute(attributeName = "total_order_price")
    private Long totalOrderPrice;

    @DynamoDBAttribute(attributeName = "total_discount_price")
    private Long totalDiscountPrice;

    @DynamoDBAttribute(attributeName = "delivery_price")
    private Long deliveryPrice;

    @DynamoDBAttribute(attributeName = "actual_price")
    private Long actualPrice;

    @DynamoDBAttribute(attributeName = "payment_date_time")
    @DynamoDBTypeConverted(converter = DynamoDbConfig.LocalDateTimeConverter.class)
    private LocalDateTime paymentDateTime;

    @DynamoDBAttribute(attributeName = "reservation_status")
    private String reservationStatus;

    @DynamoDBAttribute(attributeName = "review_status")
    private String reviewStatus;

    @DynamoDBAttribute(attributeName = "card_status")
    private String cardStatus;

}
