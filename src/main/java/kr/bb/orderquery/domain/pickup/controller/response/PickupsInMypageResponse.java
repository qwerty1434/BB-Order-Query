package kr.bb.orderquery.domain.pickup.controller.response;

import kr.bb.orderquery.domain.pickup.dto.PickupsForDateDto;
import kr.bb.orderquery.domain.pickup.dto.PickupsInMypageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupsInMypageResponse {
    private List<PickupsInMypageDto> data;

    public static PickupsInMypageResponse from(List<PickupsInMypageDto> data) {
        return PickupsInMypageResponse.builder()
                .data(data)
                .build();
    }
}
