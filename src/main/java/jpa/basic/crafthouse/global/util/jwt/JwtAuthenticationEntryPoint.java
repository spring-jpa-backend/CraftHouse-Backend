package jpa.basic.crafthouse.global.util.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jpa.basic.crafthouse.global.exception.ApiResponse;
import jpa.basic.crafthouse.global.exception.ErrorCode;
import jpa.basic.crafthouse.global.exception.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        Object exception = request.getAttribute("exception");

        if (exception instanceof ExpiredJwtException) {
            setErrorResponse(request, response, ErrorCode.EXPIRED_TOKEN);
            return;
        }

        if (exception != null) {
            setErrorResponse(request, response, ErrorCode.INVALID_TOKEN);
            return;
        }

        setErrorResponse(request, response, ErrorCode.UNAUTHORIZED_ACCESS);
    }

    private void setErrorResponse(HttpServletRequest request, HttpServletResponse response,
                                  ErrorCode errorCode) throws IOException {
        log.warn("[인증 에러] 코드: {}, 메시지: {}, 경로: {}",
                errorCode.getCode(), errorCode.getMessage(), request.getRequestURI());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(errorCode.getStatus().value());

        ApiResponse<Void> apiResponse = ApiResponse.fail(ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(errorCode.getStatus().value())
                .error(errorCode.getStatus().name())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .path(request.getRequestURI())
                .build()
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}