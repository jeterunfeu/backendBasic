package com.srlab.basic.authserverside.users.services;

import com.srlab.basic.authserverside.users.models.UserInfo;
import com.srlab.basic.authserverside.users.repositories.UserRepository;
import com.srlab.basic.authserverside.users.utils.JwtTokenProvider;
import com.srlab.basic.serverside.boards.models.RequestDto;
import com.srlab.basic.serverside.boards.models.UserResponseDto;
import com.srlab.basic.serverside.configs.YamlConfig;
import com.srlab.basic.serverside.utils.BcryptUtil;
import com.srlab.basic.serverside.utils.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private Response response;
    @Autowired
    private BcryptUtil bcryptUtil;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private YamlConfig yamlConfig;

    public ResponseEntity<?> signUp(RequestDto.SignUp signUp) {

        if (userRepository.findOneById(signUp.getId())/*.orElse(null)*/ == null) {
            return response.fail("이미 가입된 회원입니다.", HttpStatus.BAD_REQUEST);
        }

        UserInfo user = UserInfo.builder()
                .id(signUp.getId())
                .password(bcryptUtil.encodeBcrypt(signUp.getPassword(), Integer.parseInt(yamlConfig.getCount())))
                .name(signUp.getName())
                .cellPhone(signUp.getCellPhone())
                .email(signUp.getEmail())
                .address(signUp.getAddress())
                .addressDetail(signUp.getAddressDetail())
                .build();
        
        userRepository.save(user);

        return response.success("회원가입에 성공했습니다.");
    }

    public ResponseEntity<?> login(RequestDto.Login login) {

        UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return response.success(tokenInfo, "로그인에 성공했습니다.", HttpStatus.OK);
    }

    public ResponseEntity<?> refresh(RequestDto.Refresh refresh) {
        // 1. Refresh Token 검증
        if (!jwtTokenProvider.validateToken(refresh.getRefreshToken())) {
            return response.fail("Refresh Token 정보가 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(refresh.getAccessToken());

        // 3. Redis 에서 User email 을 기반으로 저장된 Refresh Token 값을 가져옵니다.
        String refreshToken = (String)redisTemplate.opsForValue().get("RT:" + authentication.getName());
        // (추가) 로그아웃되어 Redis 에 RefreshToken 이 존재하지 않는 경우 처리
        if(ObjectUtils.isEmpty(refreshToken)) {
            return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        }
        if(!refreshToken.equals(refresh.getRefreshToken())) {
            return response.fail("Refresh Token 정보가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 4. 새로운 토큰 생성
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // 5. RefreshToken Redis 업데이트
        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

        return response.success(tokenInfo, "Token 정보가 갱신되었습니다.", HttpStatus.OK);
    }

    public ResponseEntity<?> logout(RequestDto.Logout logout) {
        // 1. Access Token 검증
        if (!jwtTokenProvider.validateToken(logout.getAccessToken())) {
            return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(logout.getAccessToken());

        // 3. Redis 에서 해당 User email 로 저장된 Refresh Token 이 있는지 여부를 확인 후 있을 경우 삭제합니다.
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("RT:" + authentication.getName());
        }

        // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(logout.getAccessToken());
        redisTemplate.opsForValue()
                .set(logout.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);

        return response.success("로그아웃 되었습니다.");
    }

    public ResponseEntity<?> checkInfo(RequestDto.Check check) {

        // 1. Access Token 검증
        if (!jwtTokenProvider.validateToken(check.getAccessToken())) {
            return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        }

        // 2. Access Token 에서 User email 을 가져옵니다.
        Authentication authentication = jwtTokenProvider.getAuthentication(check.getAccessToken());

        return response.success(userRepository.findOneById(authentication.getName()));
    }
}
