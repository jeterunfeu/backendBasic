package com.srlab.basic.authserverside.users.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Schema(name = "token")
@ToString
@NoArgsConstructor
@Getter
public class Token {
    private String token;
    private String refreshToken;

    public Token(String token, String refreshToken) {
        this.token = token;
        this.refreshToken = refreshToken;
    }
}
