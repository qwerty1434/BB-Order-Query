package kr.bb.orderquery.domain.pickup.controller;

import kr.bb.orderquery.domain.pickup.controller.response.PickupsInMypageResponse;
import kr.bb.orderquery.domain.pickup.facade.PickupFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PickupController {
    private final PickupFacade pickupFacade;

}
