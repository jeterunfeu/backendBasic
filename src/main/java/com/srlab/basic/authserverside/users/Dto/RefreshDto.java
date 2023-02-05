package com.srlab.basic.authserverside.users.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "refreshDto")
@Getter
@Setter
public class RefreshDto {
//    @NotEmpty(message = "insert accessToken")
    private String accessToken;

    private String refreshToken;
}
