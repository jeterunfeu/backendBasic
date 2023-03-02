package com.srlab.basic.serverside.utils;

import com.srlab.basic.authserverside.users.models.UserInfo;
import com.srlab.basic.authserverside.users.repositories.UserRepository;
import com.srlab.basic.authserverside.users.utils.JwtTokenProvider;
import com.srlab.basic.serverside.boards.controllers.BoardController;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@RequiredArgsConstructor
public class UserInfoUtil {
    private static final Logger LOG = LoggerFactory.getLogger(UserInfoUtil.class);

    @Autowired
    private UserRepository uRepository;

    private final JwtTokenProvider jwtTokenProvider;

    public UserInfo getUserData(HttpServletRequest req) {
        String token = req.getHeader("Authorization");
        LOG.info("token : " + token);
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        User loginInfo = (User) authentication.getPrincipal();
        String id = loginInfo.getUsername();
        return uRepository.findOneById(id).orElse(null);
    }
}
