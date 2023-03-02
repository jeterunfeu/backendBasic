package com.srlab.basic.authserverside.users.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Schema(name = "signUpDto")
@Getter
@AllArgsConstructor
public class SignUpDto {

//    @NotEmpty(message = "id is required")
//        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")
    private String id;

    private String password;

    private String name;

    private String cellPhone;

    private String email;

    private String address;

    private String addressDetail;
}
