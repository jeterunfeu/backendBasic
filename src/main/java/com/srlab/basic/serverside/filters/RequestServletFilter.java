package com.srlab.basic.serverside.filters;

import com.srlab.basic.serverside.interceptors.RequestServletWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Order(2)
@Component
public class RequestServletFilter implements Filter {

    private final Logger LOG = LoggerFactory.getLogger(RequestServletFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        LOG.info("requestServletFilter start");

        HttpServletRequest wrapperRequest = new RequestServletWrapper((HttpServletRequest) servletRequest);
        LOG.info("requestServletFilter start2");
        LOG.info("response" + servletResponse.toString());
        filterChain.doFilter(wrapperRequest, servletResponse);

    }

}
