package com.wang.config;

import com.wang.constant.AuthConstant;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Component
public class FeignInterceptor implements RequestInterceptor {

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public void apply(RequestTemplate requestTemplate) {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String token = request.getHeader(AuthConstant.AUTHORIZATION);
        if(StringUtils.hasText(token)){
               requestTemplate.header(AuthConstant.AUTHORIZATION,token);
               return;
        }
         String globalToken = UUID.randomUUID().toString();
          redisTemplate.opsForValue().set(globalToken,"");
          requestTemplate.header(AuthConstant.GLOBAL_TOKEN_HEADER,globalToken);

    }
}
