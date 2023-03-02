package com.srlab.basic.authserverside.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "checkDto")
@Getter
@Setter
public class CheckDto {
//    @NotEmpty(message = "bad request")
    private String accessToken;

    private String refreshToken;
}
