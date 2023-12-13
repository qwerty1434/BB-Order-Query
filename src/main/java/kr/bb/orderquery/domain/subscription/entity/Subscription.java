package kr.bb.orderquery.domain.subscription.entity;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import kr.bb.orderquery.config.DynamoDbConfig;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@DynamoDBTable(tableName="Subscription")
public class Subscription {
    @Id
    @DynamoDBHashKey(attributeName = "subscription_id")
    private String subscriptionId;

    @DynamoDBIndexRangeKey(attributeName = "next_payment_date", globalSecondaryIndexNames = {"byUserId","byNextDeliveryDate"})
    @DynamoDBTypeConverted(converter = DynamoDbConfig.LocalDateConverter.class)
    private LocalDate nextPaymentDate;

    @DynamoDBIndexHashKey(attributeName = "user_id", globalSecondaryIndexName = "byUserId")
    private Long userId;

    @DynamoDBIndexHashKey(attributeName = "next_delivery_date", globalSecondaryIndexName = "byNextDeliveryDate")
    private String nextDeliveryDate;

    @DynamoDBAttribute(attributeName = "subscription_code")
    private String subscriptionCode;

    @DynamoDBAttribute(attributeName = "store_id")
    private Long storeId;

    @DynamoDBAttribute(attributeName = "store_name")
    private String storeName;

    @DynamoDBAttribute(attributeName = "product_thumbnail")
    private String productThumbnail;

    @DynamoDBAttribute(attributeName = "product_name")
    private String productName;

    @DynamoDBAttribute(attributeName = "unit_price")
    private Long unitPrice;

    @DynamoDBAttribute(attributeName = "quantity")
    private Integer quantity;

    @DynamoDBAttribute(attributeName = "orderer_name")
    private String ordererName;

    @DynamoDBAttribute(attributeName = "orderer_phone_number")
    private String ordererPhoneNumber;

    @DynamoDBAttribute(attributeName = "orderer_email")
    private String ordererEmail;

    @DynamoDBAttribute(attributeName = "recipient_name")
    private String recipientName;

    @DynamoDBAttribute(attributeName = "recipient_phone_number")
    private String recipientPhoneNumber;

    @DynamoDBAttribute(attributeName = "delivery_address")
    private String deliveryAddress;

    @DynamoDBAttribute(attributeName = "zipcode")
    private String zipcode;

    @DynamoDBAttribute(attributeName = "road_name")
    private String roadName;

    @DynamoDBAttribute(attributeName = "address_detail")
    private String addressDetail;

    @DynamoDBAttribute(attributeName = "payment_date_time")
    @DynamoDBTypeConverted(converter = DynamoDbConfig.LocalDateTimeConverter.class)
    private LocalDateTime paymentDateTime;

    @DynamoDBAttribute(attributeName = "total_order_price")
    private Long totalOrderPrice;

    @DynamoDBAttribute(attributeName = "total_discount_price")
    private Long totalDiscountPrice;

    @DynamoDBAttribute(attributeName = "delivery_price")
    private Long deliveryPrice;

    @DynamoDBAttribute(attributeName = "actual_price")
    private Long actualPrice;

    @DynamoDBAttribute(attributeName = "review_status")
    private String reviewStatus;

    @DynamoDBAttribute(attributeName = "is_unsubscribed")
    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL)
    private Boolean isUnsubscribed;

}
