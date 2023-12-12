package kr.bb.orderquery.domain.pickup.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import kr.bb.orderquery.domain.pickup.entity.Pickup;
import kr.bb.orderquery.domain.pickup.repository.PickupRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@ActiveProfiles("local")
@SpringBootTest
class PickupReaderTest {
    @Autowired
    private PickupReader pickupReader;
    @Autowired
    private PickupRepository pickupRepository;
    @Autowired
    private AmazonDynamoDB amazonDynamoDb;
    @Autowired
    private DynamoDBMapper dynamoDbMapper;


    @Test
    void readByUserId(){
        // given
        Long userId = 1L;
        List<Pickup> func = pickupReader.readByUserId(userId);
        func.forEach(
                v -> System.out.println(v.getPickupReservationId())
        );

    }
}