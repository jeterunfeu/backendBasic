package com.srlab.basic.authserverside.users.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Schema(name = "tokenDto")
@Builder
@Getter
@AllArgsConstructor
public class TokenDto {
    private String accessToken;
    private String refreshToken;
    private Long refreshTokenExpirationTime;
}
