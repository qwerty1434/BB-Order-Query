package kr.bb.orderquery.domain.pickup.repository;

import kr.bb.orderquery.domain.pickup.entity.Pickup;
import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;


@EnableScan
public interface PickupRepository extends CrudRepository<Pickup, String> {
    List<Pickup> findAllByStoreId(Long storeId);
    List<Pickup> findAllByUserId(Long userId);
    List<Pickup> findAllByStoreIdAndPickupDate(Long storeId, String pickupDate);
    Optional<Pickup> findByOrderProductId(Long orderProductId);
}
