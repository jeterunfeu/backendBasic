package com.srlab.basic.serverside.filters;

import com.srlab.basic.serverside.interceptors.RequestServletWrapper;
import com.srlab.basic.serverside.logs.models.ApiHistories;
import com.srlab.basic.serverside.logs.repositories.ApiHistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

//@Order(1)
@Component
public class RequestServletFilter implements Filter {

    private final Logger LOG = LoggerFactory.getLogger(RequestServletFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest wrapperRequest = new RequestServletWrapper((HttpServletRequest) servletRequest);

        filterChain.doFilter(wrapperRequest, servletResponse);

    }

}
