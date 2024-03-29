package kr.bb.orderquery.domain.pickup.controller;

import bloomingblooms.response.CommonResponse;
import kr.bb.orderquery.domain.pickup.controller.response.PickAndSubResponse;
import kr.bb.orderquery.domain.pickup.controller.response.PickupsForDateResponse;
import kr.bb.orderquery.domain.pickup.controller.response.PickupsInMypageResponse;
import kr.bb.orderquery.domain.pickup.dto.PickupDetailDto;
import kr.bb.orderquery.domain.pickup.facade.PickupFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class PickupController {
    private final PickupFacade pickupFacade;

    @GetMapping("/reservations")
    public CommonResponse<PickupsInMypageResponse> myPickups(@RequestHeader(value = "userId") Long userId, Pageable pageable) {
        return CommonResponse.success(pickupFacade.getPickupsOfUser(userId, pageable));
    }

    @GetMapping("/{storeId}/reservations")
    public CommonResponse<PickupsForDateResponse> pickupsForDate(@PathVariable Long storeId,
                                                                 @RequestParam String year, @RequestParam String month, @RequestParam String day) {
        LocalDate date = LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
        return CommonResponse.success(pickupFacade.getPickupsForDate(storeId, date.toString()));
    }

    @GetMapping("/reservations/{reservationId}")
    public CommonResponse<PickupDetailDto> pickupDetail(@PathVariable String reservationId) {
        return CommonResponse.success(pickupFacade.getPickupDetail(reservationId));
    }

    @GetMapping("/{storeId}/reservations/subscriptions")
    public CommonResponse<PickAndSubResponse> pickAndSubForCalendar(@PathVariable Long storeId) {
        return CommonResponse.success(pickupFacade.getDataForCalendar(storeId));
    }

}
