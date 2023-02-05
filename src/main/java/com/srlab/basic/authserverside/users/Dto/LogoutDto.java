package com.srlab.basic.authserverside.users.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "logoutDto")
@Getter
@Setter
public class LogoutDto {
//    @NotEmpty(message = "잘못된 요청입니다.")
    private String accessToken;

    private String refreshToken;
}
