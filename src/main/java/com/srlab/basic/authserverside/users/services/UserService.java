package com.srlab.basic.authserverside.users.services;

import com.srlab.basic.authserverside.users.Dto.*;
import com.srlab.basic.authserverside.users.models.User;
import com.srlab.basic.authserverside.users.repositories.UserRepository;
import com.srlab.basic.authserverside.users.utils.JwtTokenProvider;
import com.srlab.basic.serverside.configs.YamlConfig;
import com.srlab.basic.serverside.filters.RequestServletFilter;
import com.srlab.basic.serverside.logs.models.ConnectHistory;
import com.srlab.basic.serverside.logs.repositories.ConnectHistoryRepository;
import com.srlab.basic.serverside.utils.BcryptUtil;
import com.srlab.basic.serverside.utils.IpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import ua_parser.Client;
import ua_parser.Parser;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private final Logger LOG = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BcryptUtil bcryptUtil;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private ConnectHistoryRepository chRepository;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private YamlConfig yamlConfig;

    public ResponseEntity<?> signUp(SignUpDto signUp) {

        try {
            if (userRepository.findOneById(signUp.getId()).orElse(null) != null) {
                return new ResponseEntity<>("id already exists", HttpStatus.BAD_REQUEST);
            }

            User user = User.builder()
                    .id(signUp.getId())
                    .password(bcryptUtil.encodeBcrypt(signUp.getPassword(), Integer.parseInt(yamlConfig.getCount())))
                    .name(signUp.getName())
                    .cellPhone(signUp.getCellPhone())
                    .email(signUp.getEmail())
                    .address(signUp.getAddress())
                    .addressDetail(signUp.getAddressDetail())
                    .build();

            userRepository.save(user);

            return new ResponseEntity<>("sign up success", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> login(HttpServletRequest req, LoginDto login) {
        try {
            UsernamePasswordAuthenticationToken authenticationToken = login.toAuthentication();

            Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

            TokenDto tokenInfo = jwtTokenProvider.generateToken(authentication);

            redisTemplate.opsForValue()
                    .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(),
                            tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);

            checkInOut(req, null, "login");
            return new ResponseEntity<>("sign up success", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> refresh(HttpServletRequest req, RefreshDto refresh) {
        try {
            // validate Refresh Token
            if (!jwtTokenProvider.validateToken(refresh.getRefreshToken())) {
                return new ResponseEntity<>("refresh token did not validate", HttpStatus.BAD_REQUEST);
            }

            // get User email from Access Token
            Authentication authentication = jwtTokenProvider.getAuthentication(refresh.getAccessToken());

            // get Refresh Token value that based on user email at redis
            String refreshToken = (String) redisTemplate.opsForValue().get("RT:" + authentication.getName());
            // process at RefreshToken does not exist at redis
            if (ObjectUtils.isEmpty(refreshToken)) {
                return new ResponseEntity<>("it is bad request", HttpStatus.BAD_REQUEST);
            }
            if (!refreshToken.equals(refresh.getRefreshToken())) {
                return new ResponseEntity<>("refresh token info is not correct", HttpStatus.BAD_REQUEST);
            }

            // make new token
            TokenDto tokenInfo = jwtTokenProvider.generateToken(authentication);

            // RefreshToken Redis update
            redisTemplate.opsForValue()
                    .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
            checkInOut(req, null, "refresh");
            return new ResponseEntity<>("token info refreshed", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> logout(HttpServletRequest req, LogoutDto logout) {
        try {
            // validate Access Token
            if (!jwtTokenProvider.validateToken(logout.getAccessToken())) {
                return new ResponseEntity<>("bad request", HttpStatus.BAD_REQUEST);
            }

            // get User email from Access Token
            Authentication authentication = jwtTokenProvider.getAuthentication(logout.getAccessToken());

            // check Refresh Token that stored the user email at redis then delete
            if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
                // Refresh Token delete
                redisTemplate.delete("RT:" + authentication.getName());
            }

            // bring the Access Token time limit then store into blacklist
            Long expiration = jwtTokenProvider.getExpiration(logout.getAccessToken());
            redisTemplate.opsForValue()
                    .set(logout.getAccessToken(), "logout", expiration, TimeUnit.MILLISECONDS);
            checkInOut(req, null, "logout");
            return new ResponseEntity<>("logout success", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> checkInfo(CheckDto check) {
        try {
            // validate Access Token
            if (!jwtTokenProvider.validateToken(check.getAccessToken())) {
                return new ResponseEntity<>("bad request", HttpStatus.BAD_REQUEST);
            }

            // bring User email from access token
            Authentication authentication = jwtTokenProvider.getAuthentication(check.getAccessToken());

            return new ResponseEntity<>(userRepository.findOneById(authentication.getName()), HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void checkInOut(HttpServletRequest req, Long id, String status) {
        String userAgent = req.getHeader("user-agent");

        Parser uaParser = new Parser();
        Client c = uaParser.parse(userAgent);

        String browser = c.userAgent.family;
        String bVersion = c.userAgent.major + "." + c.userAgent.minor;
        String os = c.os.family;
        String osVersion = c.os.major + "." + c.os.minor;
        String device = c.device.family;

        ConnectHistory cHistory = ConnectHistory.builder()
                .userIp(IpUtil.getClientIp(req))
                .exeDate(new Date())
                .userId(id)
                .browserName(browser)
                .browserVersion(bVersion)
                .osName(os)
                .osVersion(osVersion)
                .device(device)
                .status(status)
                .build();

        chRepository.save(cHistory);
    }
}
