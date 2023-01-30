package com.srlab.basic.authserverside.users.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.srlab.basic.authserverside.users.models.Token;
import com.srlab.basic.authserverside.users.models.UserDto;
import com.srlab.basic.authserverside.users.models.UserInfo;
import com.srlab.basic.authserverside.users.repositories.UserRepository;
import com.srlab.basic.authserverside.users.services.TokenService;
import com.srlab.basic.serverside.boards.controllers.BoardController;
import com.srlab.basic.serverside.boards.models.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final Logger LOG = LoggerFactory.getLogger(BoardController.class);

    private final TokenService tokenService;
    private final UserRequestMapper userRequestMapper;
//    private final ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User)authentication.getPrincipal();
        UserDto userDto = userRequestMapper.toDto(oAuth2User);

        // 최초 로그인이라면 회원가입 처리를 한다.
        // 일단 기본정보를 넣고 추가로 넣을것
        if(userRepository.findByEmail(userDto.getEmail()) == null) {
            UserInfo user = UserInfo.builder()
                            .name(userDto.getName())
                            .email(userDto.getEmail())
                            .build();

            userRepository.save(user);
        }

//        Token token = tokenService.generateToken(userDto.getEmail(), "USER");
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        LOG.info("{}", tokenInfo);
        //redis 에 넣는다
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

    }

//    private void writeTokenResponse(HttpServletResponse response, Token token)
//            throws IOException {
//        response.setContentType("text/html;charset=UTF-8");
//
//        response.addHeader("Auth", token.getToken());
//        response.addHeader("Refresh", token.getRefreshToken());
//        response.setContentType("application/json;charset=UTF-8");
//
//        var writer = response.getWriter();
//        writer.println(objectMapper.writeValueAsString(token));
//        writer.flush();
//    }
}
