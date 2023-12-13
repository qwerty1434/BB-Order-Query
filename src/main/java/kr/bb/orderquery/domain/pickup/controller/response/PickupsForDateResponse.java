package kr.bb.orderquery.domain.pickup.controller.response;

import kr.bb.orderquery.domain.pickup.dto.PickupsForDateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupsForDateResponse {
    private List<PickupsForDateDto> data;

    public static PickupsForDateResponse from(List<PickupsForDateDto> data) {
        return PickupsForDateResponse.builder()
                .data(data)
                .build();
    }
}
