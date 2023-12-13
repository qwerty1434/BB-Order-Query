package kr.bb.orderquery.domain.subscription.exception;

import kr.bb.orderquery.exception.CustomException;

public class SubscriptionNotFoundException extends CustomException {
    private static final String MESSAGE = "해당 구독이 존재하지 않습니다.";

    public SubscriptionNotFoundException() {
        super(MESSAGE);
    }
}
