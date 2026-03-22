package jpa.basic.crafthouse.global.util;

import lombok.Builder;

@Builder
public record LoginUserInfoDto(
        Long id
) {
}