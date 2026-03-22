package jpa.basic.crafthouse.member.exception;

import jpa.basic.crafthouse.global.exception.ErrorCode;

public class MemberException extends RuntimeException {

    private final ErrorCode errorCode;

    public MemberException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
