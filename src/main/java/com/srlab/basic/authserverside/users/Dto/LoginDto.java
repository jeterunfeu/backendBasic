package com.srlab.basic.authserverside.users.Dto;

import com.srlab.basic.authserverside.users.services.UserService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;

@Schema(name = "loginDto")
@Getter
@Setter
public class LoginDto {
//    @NotEmpty(message = "Id is required")
//        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+.[A-Za-z]{2,6}$", message = "이메일 형식에 맞지 않습니다.")

//    private final UserService uService;

    private String id;

    private String password;

//    public UsernamePasswordAuthenticationToken toAuthentication(String id) {
//        UserDetails userDetails = uService.loadUserByUsername(id);
//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
//                userDetails, null, userDetails.getAuthorities());
//        return new UsernamePasswordAuthenticationToken(id, password);
//    }
}
