package jpa.basic.crafthouse.global.util.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jpa.basic.crafthouse.global.util.LoginUserInfoDto;
import jpa.basic.crafthouse.global.util.security.AuthConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = resolveToken(request);

        try {
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {
                Long   memberId = jwtTokenProvider.getMemberId(token);
                String role     = jwtTokenProvider.getRole(token);

                LoginUserInfoDto loginUser = LoginUserInfoDto.builder()
                        .id(memberId)
                        .build();

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        loginUser,
                        null,
                        Collections.singletonList(
                                new SimpleGrantedAuthority(StringUtils.hasText(role) ? role : "ROLE_USER")
                        )
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("Security Context에 '{}' 인증 정보를 저장했습니다. (uri: {})", memberId, request.getRequestURI());
            }
        } catch (ExpiredJwtException e) {
            // 핵심: 예외를 바로 던지지 않고 request에 보관 → EntryPoint가 꺼내서 처리
            log.warn("만료된 JWT 토큰입니다: {}", e.getMessage());
            request.setAttribute("exception", e);
        } catch (SecurityException | MalformedJwtException | SignatureException | UnsupportedJwtException e) {
            log.warn("잘못된 JWT 서명입니다: {}", e.getMessage());
            request.setAttribute("exception", e);
        } catch (Exception e) {
            log.error("JWT 검증 중 알 수 없는 예외가 발생했습니다: {}", e.getMessage());
            request.setAttribute("exception", e);
        }

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AuthConstants.AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(AuthConstants.BEARER_PREFIX)) {
            return bearerToken.substring(AuthConstants.BEARER_PREFIX.length());
        }
        return null;
    }
}