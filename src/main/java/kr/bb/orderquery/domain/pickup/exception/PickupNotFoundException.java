package kr.bb.orderquery.domain.pickup.exception;

import kr.bb.orderquery.exception.CustomException;

public class PickupNotFoundException extends CustomException {
    private static final String MESSAGE = "해당 픽업예약이 존재하지 않습니다.";

    public PickupNotFoundException() {
        super(MESSAGE);
    }
}
