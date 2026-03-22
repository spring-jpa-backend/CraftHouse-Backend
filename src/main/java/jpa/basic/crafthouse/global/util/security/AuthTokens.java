package jpa.basic.crafthouse.global.util.security;

public record AuthTokens(
        String accessToken,
        String refreshToken
) {}