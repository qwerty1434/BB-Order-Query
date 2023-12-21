package kr.bb.orderquery.domain.pickup.controller;

import bloomingblooms.response.CommonResponse;
import kr.bb.orderquery.domain.pickup.controller.response.PickupsForDateResponse;
import kr.bb.orderquery.domain.pickup.controller.response.PickupsInMypageResponse;
import kr.bb.orderquery.domain.pickup.dto.PickupDetailDto;
import kr.bb.orderquery.domain.pickup.facade.PickupFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class PickupController {
    private final PickupFacade pickupFacade;

    @GetMapping("/reservations")
    public CommonResponse<PickupsInMypageResponse> myPickups(@RequestHeader(value = "userId") Long userId) {
        // TODO : 페이지네이션
        return CommonResponse.success(pickupFacade.getPickupsOfUser(userId));
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

}
