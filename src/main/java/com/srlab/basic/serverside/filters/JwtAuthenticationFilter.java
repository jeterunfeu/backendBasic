package com.srlab.basic.serverside.filters;

import com.srlab.basic.authserverside.users.models.UserInfo;
import com.srlab.basic.authserverside.users.models.UserRole;
import com.srlab.basic.authserverside.users.repositories.UserRepository;
import com.srlab.basic.authserverside.users.utils.JwtTokenProvider;
import com.srlab.basic.serverside.hierarchies.models.HierarchyData;
import com.srlab.basic.serverside.logs.models.ApiHistories;
import com.srlab.basic.serverside.logs.repositories.ApiHistoryRepository;
import com.srlab.basic.serverside.utils.IpUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.naming.AuthenticationException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.List;

//@Order(2)
//@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final UserRepository uRepository;
    private final ApiHistoryRepository apiRepository;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    private String id = null;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            HttpServletRequest req = (HttpServletRequest) request;
            HttpServletResponse res = (HttpServletResponse) response;

            //jwt authorization
            LOG.info("jwt start");
            //jwt token from header
            String token = resolveToken((HttpServletRequest) request);
            LOG.info("1");
            // token check by validateToken
            if (token != null && jwtTokenProvider.validateToken(token)) {
                LOG.info("2");
                // Redis accessToken logout check
                String isLogout = (String) redisTemplate.opsForValue().get(token);
                if (ObjectUtils.isEmpty(isLogout)) {
                    LOG.info("3");
                    // get Authentication instance from token then store to SecurityContext
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    checkRole(req, res, authentication);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    LOG.info("4");
                }
//                else {
//                    flush(req, res, HttpStatus.BAD_REQUEST, "logout null");
//                }
            }

            chain.doFilter(request, response);

            if(req.getRequestURI().contains("/api/")) apiLog(req, res, id);

        } catch (Exception e) {
//            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
        }
    }

    // get token info from header
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
//        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
//            return bearerToken.substring(7);
//        }
        if(StringUtils.hasText(bearerToken)) {
            return bearerToken;
        }
        return null;
    }

//    private String flush(HttpServletRequest req, HttpServletResponse res, HttpStatus status, String message) {
//        try {
//            res.setStatus(status.value());
//            res.getWriter().append(message).flush();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

    private void checkRole(HttpServletRequest req, HttpServletResponse res, Authentication auth){
        try {
            LOG.info("principal : " + auth.getPrincipal().toString());
           User loginInfo = (User) auth.getPrincipal();
           id = loginInfo.getUsername();
           UserInfo user = uRepository.findOneById(id).orElse(null);
           HierarchyData data = user.getHierarchyData();
           List<UserRole> roles = data.getRoles();
           String uri = req.getRequestURI();
           Boolean check = false;

            if(data.getName().equals("developer")) {
                LOG.info("DEVELOPER! freepass");
                check = true;
            } else {
                LOG.info("check pass");
                for (UserRole value : roles) {
//               if(data.getName().equals("developer")) {
//                   check = true;
//                   break;
//               }
                    check = uri.matches(value.getUrl());
                }
            }

           if(!check) {
//               flush(req, res, HttpStatus.UNAUTHORIZED, "unauthorized");
               throw new RuntimeException("unauthorized");
           }

        } catch (Exception e) {
//            e.printStackTrace();
            throw new RuntimeException(e.getMessage());
//            flush(req, res, HttpStatus.UNAUTHORIZED, "unauthorized");
        }
    }

    private String apiLog(HttpServletRequest req, HttpServletResponse res, String id) {
        try {
            //api log
            String uri = req.getRequestURI();
            String body = null;
            String ip = IpUtil.getClientIp(req);

//            if(!uri.equals("/error")) body = getBody(req);

            ApiHistories api = ApiHistories.builder()
                    .method(req.getMethod())
                    .path(uri)
                    .search(req.getQueryString())
                    .body(null)
                    .status(res.getStatus())
                    .userId(id)
                    .userIp(ip)
                    .exeDate(new Date())
                    .build();

            apiRepository.save(api);
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getBody(HttpServletRequest request) throws IOException {
        BufferedReader buff = null;
        StringBuilder builder;
        String result = null;
        try{
            buff = new BufferedReader(new InputStreamReader(request.getInputStream()));
            builder = new StringBuilder();
            String buffer;
            while ((buffer = buff.readLine()) != null) {
                if (builder.length() > 0) {
                    builder.append("\n");
                }
                builder.append(buffer);
            }
            result = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(buff != null) buff.close();
        }
        return result;
    }

}