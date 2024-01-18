package kr.bb.orderquery.domain.subscription.repository;

import kr.bb.orderquery.domain.subscription.entity.Subscription;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableScan
public interface SubscriptionRepository extends CrudRepository<Subscription, String> {
    List<Subscription> findAllByStoreId(Long storeId);
    List<Subscription> findAllByUserIdOrderByNextPaymentDateDesc(Long userId);
    List<Subscription> findAllByStoreIdAndNextDeliveryDate(Long storeId, String nextDeliveryDate);
}
