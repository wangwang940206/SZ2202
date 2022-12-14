package com.wang.config;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wang.constant.AuthConstant;
import com.wang.constant.model.AuthResult;
import com.wang.constant.model.Result;
import com.wang.ex.LoginException;
import com.wang.service.impl.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.UUID;

@Configuration
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;
    @Autowired
    private StringRedisTemplate redisTemplate;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //??????????????????
        http.csrf().disable();
        //????????????
        http.cors().disable();
        //??????session
        http.sessionManagement().disable();
        //????????????
        http.formLogin().loginProcessingUrl(AuthConstant.LOGIN_URL).successHandler(authenticationSuccessHandler())
                .failureHandler(authenticationFailureHandler())
                .permitAll();

        //??????
        http.logout().logoutUrl(AuthConstant.LOGIN_OUT).logoutSuccessHandler(logoutSuccessHandler()).permitAll();

        //??????????????????????????????????????????
        http.authorizeRequests().anyRequest().authenticated();

    }

    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {
            String token = UUID.randomUUID().toString();
            String authenticationStr = JSON.toJSONString(authentication);
            redisTemplate.opsForValue().set(AuthConstant.TOKEN_PREFIX + token, authenticationStr, Duration.ofSeconds(AuthConstant.TOKEN_EXPIRE_TIME));
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_NDJSON_VALUE);
            AuthResult authResult = new AuthResult();
            authResult.setAccessToken(token);
            authResult.setExpiresIn(AuthConstant.TOKEN_EXPIRE_TIME);
            authResult.setType(AuthConstant.BEARER);
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(authResult);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.flush();
            writer.close();

        };
    }

    /**
     * ?????????????????????
     *
     * @return
     */
    @Bean
    public AuthenticationFailureHandler authenticationFailureHandler() {
        return (request, response, exception) -> {
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Result<String> result = new Result<>();
            result.setCode(HttpStatus.UNAUTHORIZED.value());
            if (exception instanceof LockedException) {
                result.setMsg("?????????????????????????????????");
            } else if (exception instanceof BadCredentialsException) {
                result.setMsg("??????????????????????????????????????????");
            } else if (exception instanceof DisabledException) {
                result.setMsg("?????????????????????????????????");
            } else if (exception instanceof AccountExpiredException) {
                result.setMsg("?????????????????????????????????");
            } else if (exception instanceof CredentialsExpiredException) {
                result.setMsg("?????????????????????????????????");
            } else if (exception instanceof LoginException) {
                result.setMsg(exception.getMessage());
            } else {
                result.setMsg("????????????");
            }
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(result);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.flush();
            writer.close();
        };
    }

    @Bean
    public LogoutSuccessHandler logoutSuccessHandler() {
        return (request, response, authentication) -> {
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            // ???token
            String token = request.getHeader(AuthConstant.AUTHORIZATION);
            String realToken = token.replaceFirst(AuthConstant.BEARER, "");
            redisTemplate.delete(AuthConstant.TOKEN_PREFIX + realToken);
            Result<String> result = Result.success("????????????");
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(result);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.flush();
            writer.close();
        };
    }
}
