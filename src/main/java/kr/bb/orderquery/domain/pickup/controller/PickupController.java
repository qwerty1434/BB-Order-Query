package kr.bb.orderquery.domain.pickup.controller;

import kr.bb.orderquery.domain.pickup.controller.response.PickupsForDateResponse;
import kr.bb.orderquery.domain.pickup.controller.response.PickupsInMypageResponse;
import kr.bb.orderquery.domain.pickup.dto.PickupDetailDto;
import kr.bb.orderquery.domain.pickup.facade.PickupFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class PickupController {
    private final PickupFacade pickupFacade;

    @GetMapping("/reservations")
    public ResponseEntity<PickupsInMypageResponse> myPickups(@RequestHeader(value = "userId") Long userId) {
        // TODO : 페이지네이션
        return ResponseEntity.ok().body(pickupFacade.getPickupsForUser(userId));
    }

    @GetMapping("/{storeId}/reservations")
    public ResponseEntity<PickupsForDateResponse> pickupsForDate(@PathVariable Long storeId,
                                                                 @RequestParam String year, @RequestParam String month, @RequestParam String day) {
        LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        return ResponseEntity.ok().body(pickupFacade.getPickupsForDate(storeId, date.toString()));
    }

    @GetMapping("/reservations/{reservationId}")
    public ResponseEntity<PickupDetailDto> pickupDetail(@PathVariable String reservationId) {
        return ResponseEntity.ok().body(pickupFacade.getPickupDetail(reservationId));
    }

}
