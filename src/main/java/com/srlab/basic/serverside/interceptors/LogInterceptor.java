package com.srlab.basic.serverside.interceptors;

import com.srlab.basic.serverside.logs.models.ApiHistories;
import com.srlab.basic.serverside.logs.repositories.ApiHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@RequiredArgsConstructor
public class LogInterceptor implements HandlerInterceptor {

    private final Logger LOG = LoggerFactory.getLogger(LogInterceptor.class);

    @Autowired
    private final ApiHistoryRepository apiRepository;
    
    //인터셉터 없애고 jwt filter에 구현할 것

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
////        return HandlerInterceptor.super.preHandle(request, response, handler);
//        StringBuilder sb = new StringBuilder();
//        String body;
//
//        while (StringUtils.hasText(body = new BufferedReader(new InputStreamReader(request.getInputStream())).readLine())) {
//            sb.append(body);
//        }
//
//        apiRepository.save(new ApiHistories(request.getMethod(), request.getRequestURI()
//                , request.getQueryString(), sb.toString(), response.getStatus(), null, null));
//
//        LOG.info("passed");
//        return true;
//
//    }

//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        HandlerInterceptor.super.postHandle(request, response, handler, modelAndView);
//    }

//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
//    }

}
