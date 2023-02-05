package com.srlab.basic.authserverside.users.utils;

import com.srlab.basic.authserverside.users.Dto.OAuthDto;
import com.srlab.basic.authserverside.users.Dto.TokenDto;
import com.srlab.basic.authserverside.users.models.User;
import com.srlab.basic.authserverside.users.repositories.UserRepository;
import com.srlab.basic.authserverside.users.services.TokenService;
import com.srlab.basic.serverside.boards.controllers.BoardController;
import com.srlab.basic.serverside.configs.YamlConfig;
import com.srlab.basic.serverside.utils.BcryptUtil;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.RandomStringGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final Logger LOG = LoggerFactory.getLogger(BoardController.class);

    private final TokenService tokenService;
    private final UserRequestMapper userRequestMapper;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BcryptUtil bcryptUtil;
    @Autowired
    private YamlConfig yamlConfig;

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        OAuthDto oAuthDto = userRequestMapper.toDto(oAuth2User);
        String email = oAuthDto.getEmail();
        //make password
        RandomStringGenerator random = new RandomStringGenerator.Builder().withinRange(33, 45).build();
        String pw = random.generate(12);

        // sign up process. later write more info
        if (email != null && userRepository.findOneById(email) == null) {
            User user = User.builder()
                    .id(email)
                    .password(bcryptUtil.encodeBcrypt(pw, Integer.parseInt(yamlConfig.getCount())))
                    .name(oAuthDto.getName())
                    .email(oAuthDto.getEmail())
                    .build();

            userRepository.save(user);
        } else {
            throw new RuntimeException("information null");
        }

        TokenDto tokenInfo = jwtTokenProvider.generateToken(authentication);
        //put into redis
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(),
                        tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

    }
}
