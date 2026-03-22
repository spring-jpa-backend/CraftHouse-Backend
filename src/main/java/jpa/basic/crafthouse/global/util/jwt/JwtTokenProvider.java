package jpa.basic.crafthouse.global.util.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String ISSUER = "CraftHouse";

    @Value("${jwt.secret-key}")
    private String secretKeyString;

    @Value("${jwt.access-token-validity-time}")
    private long accessTokenValidityInMilliseconds;

    @Value("${jwt.refresh-token-validity-time}")
    private long refreshTokenValidityInMilliseconds;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        if (keyBytes.length < 32) {
            throw new IllegalArgumentException("JWT secret-key는 최소 32바이트(256bit) 이상이어야 합니다.");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        log.info("HS256 대칭키가 성공적으로 초기화되었습니다.");
    }

    public String createAccessToken(Long memberId, String role) {
        return buildToken(memberId, role, accessTokenValidityInMilliseconds);
    }

    public String createRefreshToken(Long memberId) {
        return buildToken(memberId, null, refreshTokenValidityInMilliseconds);
    }

    private String buildToken(Long memberId, String role, long validityTimeInMs) {
        Date now      = new Date();
        Date validity = new Date(now.getTime() + validityTimeInMs);

        var builder = Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuer(ISSUER)
                .issuedAt(now)
                .expiration(validity)
                .id(UUID.randomUUID().toString());

        if (role != null) {
            builder.claim("role", role); // Custom Claim 추가
        }

        return builder
                .signWith(key)
                .compact();
    }

    public Long getMemberId(String token) {
        String subject = Jwts.parser()
                .verifyWith(key)
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return Long.parseLong(subject);
    }

    public String getRole(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .requireIssuer(ISSUER)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public int getRefreshTokenValidityInSeconds() {
        return (int) (refreshTokenValidityInMilliseconds / 1000);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .requireIssuer(ISSUER)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException | SignatureException e) {
            log.warn("잘못된 JWT 서명입니다 (위조 의심): {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.info("만료된 JWT 토큰입니다: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다: {}", e.getMessage());
        }
        return false;
    }
}