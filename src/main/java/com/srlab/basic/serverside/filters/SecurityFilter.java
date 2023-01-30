package com.srlab.basic.serverside.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(1)
@Component
public class SecurityFilter implements Filter {

    private final Logger LOG = LoggerFactory.getLogger(SecurityFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        LOG.info("cors filter start");

        //cors security
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//
//        if (request.getHeader("Origin") != null) {
//            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
//        }
//
//        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Methods","GET, OPTIONS, PUT, DELETE, POST, PATCH");
//        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Headers","X-Requested-With, Content-Type");
//        ((HttpServletResponse) servletResponse).addHeader("Access-Control-Allow-Credentials","true");
//
//        HttpServletResponse resp = (HttpServletResponse) servletResponse;
//
//        if (request.getMethod().equals("OPTIONS")) {
//            resp.setStatus(HttpServletResponse.SC_OK);
//            return;
//        }

        filterChain.doFilter(request, servletResponse);

    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
