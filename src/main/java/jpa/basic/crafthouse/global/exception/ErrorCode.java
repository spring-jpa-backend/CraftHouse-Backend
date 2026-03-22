package jpa.basic.crafthouse.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "A001", "접근 권한이 없습니다."),
    EXPIRED_TOKEN(HttpStatus.BAD_REQUEST, "A002", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "A003", "잘못된 토큰 형식입니다.");

    HttpStatus status;
    String code;
    String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
    }
}
