package com.srlab.basic.authserverside.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(name = "oAuthDto")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OAuthDto {
    private String email;
    private String name;
}
