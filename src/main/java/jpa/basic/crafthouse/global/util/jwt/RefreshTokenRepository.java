package jpa.basic.crafthouse.global.util.jwt;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);      // 재발급 시 토큰 대조용

    Optional<RefreshToken> findByMemberId(Long memberId); // 로그인 시 기존 토큰 조회용
}