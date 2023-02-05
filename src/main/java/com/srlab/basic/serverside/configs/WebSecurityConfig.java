package com.srlab.basic.serverside.configs;

import com.srlab.basic.authserverside.users.repositories.UserRepository;
import com.srlab.basic.authserverside.users.services.CustomOAuth2UserService;
import com.srlab.basic.authserverside.users.utils.JwtTokenProvider;
import com.srlab.basic.authserverside.users.utils.OAuth2SuccessHandler;
import com.srlab.basic.serverside.filters.JwtAuthenticationFilter;
import com.srlab.basic.serverside.logs.repositories.ApiHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@RequiredArgsConstructor
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    private final Logger LOG = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Autowired
    private ApiHistoryRepository apiRepository;
    @Autowired
    private UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;

    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler successHandler;

    @Bean
//    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        LOG.info("security config start");
        httpSecurity
                .httpBasic().disable()
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers(/*"/api/tokens/**"*/"/api/**", "/css/**",
                        "/js/**", "/*.ico", "/*.html**", "/error").permitAll()
                .antMatchers("/v2/api-docs", "/swagger-resources/**",
                        "/swagger-ui/**", "/webjars/**","/swagger/**").permitAll()
                .anyRequest().authenticated()
                .and()
                .oauth2Login()
                .successHandler(successHandler)
                .userInfoEndpoint().userService(oAuth2UserService);

        httpSecurity.addFilterBefore(new JwtAuthenticationFilter(userRepository, apiRepository, jwtTokenProvider, redisTemplate), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    //                .antMatchers("/api/hierarchies/**").permitAll()
//                .anyRequest().authenticated()
    //login form address + success fail forwarding
//                .and()
//                .formLogin()
//                .defaultSuccessUrl("/loginsuccess")
//                .failureUrl("/fail")
//                .permitAll()
//                .and()

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "OPTIONS", "DELETE", "PUT", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("X-Requested-With", "Origin", "Content-Type", "Accept", "Authorization"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}