package jpabook.jpashop.exhandler;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    FAIL("ERROR_CODE_0000", "유효성 체크 실패"),
    NOT_NULL("ERROR_CODE_0001", "필수값이 누락되었습니다"),
    MIN_VALUE("ERROR_CODE_0002", "최소값 커야 합니다."),
    RANGE("ERROR_CODE_0003", "범위가 맞지 않습니다.");

    private final String code;
    private final String message;
}
