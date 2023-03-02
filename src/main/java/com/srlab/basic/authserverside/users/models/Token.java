package com.srlab.basic.authserverside.users.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Schema(name = "token")
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Token {
    private String token;
    private String refreshToken;

}
