package com.wang.filter;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wang.constant.AuthConstant;
import com.wang.constant.ResourceConstant;
import com.wang.constant.model.Result;
import com.wang.domain.LoginSysUser;
import com.wang.utils.PathMacthUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import sun.plugin.liveconnect.SecurityContextHelper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class TokenTransferFilter extends OncePerRequestFilter {
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        if(PathMacthUtils.isMatch(ResourceConstant.RESOURCE_ALLOW_URLS,path)){
                 filterChain.doFilter(request,response);
                 return;
        }
      if(PathMacthUtils.isMatch(ResourceConstant.BUSINESS_ALLOW_URLS,path)){
          String myGlobalKey = request.getHeader(AuthConstant.GLOBAL_TOKEN_HEADER);
           if(StringUtils.hasText(myGlobalKey)){
                    if(redisTemplate.hasKey(myGlobalKey)){
                        redisTemplate.delete(myGlobalKey);
                        filterChain.doFilter(request,response);
                        return;
                    }
           }

      }

        String auth = request.getHeader(AuthConstant.AUTHORIZATION);
        String token = auth.replace(AuthConstant.BEARER, "");
        Long expire = redisTemplate.getExpire(AuthConstant.TOKEN_PREFIX + token);
       if(!ObjectUtils.isEmpty(expire)&& !expire.equals(-2)){
               if(expire<AuthConstant.TOKEN_EXPIRE_THRESHOLD){
                 redisTemplate.expire(AuthConstant.TOKEN_PREFIX + token, Duration.ofSeconds(AuthConstant.TOKEN_EXPIRE_TIME));
               }
           String str = redisTemplate.opsForValue().get(AuthConstant.TOKEN_PREFIX + token);
           UsernamePasswordAuthenticationToken authenticationToken = JSON.parseObject(str, UsernamePasswordAuthenticationToken.class);
           LoginSysUser loginSysUser = JSON.parseObject(authenticationToken.getPrincipal().toString(), LoginSysUser.class);
           Set<String> auths = loginSysUser.getAuths();
           List<SimpleGrantedAuthority> grantedAuthorities = auths.parallelStream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
           UsernamePasswordAuthenticationToken authenticationToken1 = new UsernamePasswordAuthenticationToken(loginSysUser, null, grantedAuthorities);
           SecurityContextHolder.getContext().setAuthentication(authenticationToken1);
           filterChain.doFilter(request,response);
           return;


       }
        // 有问题 拦截 报错
        response.setContentType("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        Result<String> result = Result.fail(HttpStatus.UNAUTHORIZED.value(), "token转换异常");
        ObjectMapper objectMapper = new ObjectMapper();
        String s = objectMapper.writeValueAsString(result);
        PrintWriter writer = response.getWriter();
        writer.write(s);
        writer.flush();
        writer.close();



    }
}
