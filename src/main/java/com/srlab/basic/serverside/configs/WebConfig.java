package com.srlab.basic.serverside.configs;

import com.navercorp.lucy.security.xss.servletfilter.XssEscapeServletFilter;
import com.srlab.basic.serverside.logs.repositories.ApiHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.SortHandlerMethodArgumentResolver;
import org.springframework.http.CacheControl;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Duration;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final Logger LOG = LoggerFactory.getLogger(WebConfig.class);

    @Autowired
    private final ApiHistoryRepository apiRepository;

    //interceptor configuration
//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor(new LogInterceptor(apiRepository))
//                .order(1)
//                .addPathPatterns("/**")
//                .excludePathPatterns("/css/**", "/*.ico", "/error");
//    }

    //web cache configuration
    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        CacheControl cacheControl = CacheControl.maxAge(Duration.ofDays(365));

        registry.addResourceHandler("/resources/**")
                .addResourceLocations("classpath:/static/")
                .setCacheControl(cacheControl);
    }

    //xss configuration
    @Bean
    public FilterRegistrationBean<XssEscapeServletFilter> filterRegistrationBean() {
        FilterRegistrationBean<XssEscapeServletFilter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter(new XssEscapeServletFilter());
        filterRegistration.setOrder(1);
        filterRegistration.addUrlPatterns("/*");
        return filterRegistration;
    }

    //pageable
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        SortHandlerMethodArgumentResolver sortArgumentResolver = new SortHandlerMethodArgumentResolver();
        sortArgumentResolver.setSortParameter("sortBy");
        sortArgumentResolver.setPropertyDelimiter("-");

        PageableHandlerMethodArgumentResolver pageableArgumentResolver = new PageableHandlerMethodArgumentResolver(sortArgumentResolver);
        pageableArgumentResolver.setOneIndexedParameters(true);
        pageableArgumentResolver.setMaxPageSize(500);
        pageableArgumentResolver.setFallbackPageable(PageRequest.of(0,10));
        argumentResolvers.add(pageableArgumentResolver);

    }

}
