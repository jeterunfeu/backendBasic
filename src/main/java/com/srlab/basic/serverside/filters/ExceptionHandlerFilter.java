package com.srlab.basic.serverside.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ExceptionHandlerFilter extends OncePerRequestFilter {
    private final Logger LOG = LoggerFactory.getLogger(ExceptionHandlerFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try{
            LOG.info("i1");
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e){
            LOG.info("i2");
            //토큰의 유효기간 만료
            setErrorResponse(response, e.getMessage(), HttpStatus.UNAUTHORIZED);
        }catch (JwtException | IllegalArgumentException e){
            LOG.info("i3");
            //유효하지 않은 토큰
            setErrorResponse(response, e.getMessage(), HttpStatus.BAD_REQUEST);
        }catch (Exception e) {
            LOG.info("i4");
            setErrorResponse(response, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    private void setErrorResponse(
            HttpServletResponse response,
            String message,
            HttpStatus errorCode
    ){
        ObjectMapper objectMapper = new ObjectMapper();
        response.setStatus(errorCode.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = new ErrorResponse(errorCode.value(), message);
        try{
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Data
    public static class ErrorResponse{
        private final Integer code;
        private final String message;
    }
}