package kr.bb.orderquery.domain.pickup.controller.response;

import kr.bb.orderquery.domain.pickup.dto.PickupsInMypageDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.util.List;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PickupsInMypageResponse {
    private List<PickupsInMypageDto> data;
    private Long totalCnt;

    public static PickupsInMypageResponse of(Page<PickupsInMypageDto> pageData) {
        return PickupsInMypageResponse.builder()
                .data(pageData.getContent())
                .totalCnt(pageData.getTotalElements())
                .build();
    }
}
