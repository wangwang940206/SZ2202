package com.wang.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wang.constant.AuthConstant;
import com.wang.constant.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

/*
* 网关全局TOKEN校验
* */
@Component
public class AuthFilter implements GlobalFilter,Ordered {
    @Autowired
    private StringRedisTemplate redisTemplate;

    /**和前端约定好token放header中 Authorization :bearer
     *
     * @param exchange
     * @param chain
     * @return
     */

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();
  if(AuthConstant.ALLOE_URL.contains(path)){
      return chain.filter(exchange);
      
  }
        String authorization = request.getHeaders().getFirst(AuthConstant.AUTHORIZATION);
         if(StringUtils.hasText(authorization)){
             String token = authorization.replaceAll(AuthConstant.BEARER, "");
             if(StringUtils.hasText(token)&&redisTemplate.hasKey(AuthConstant.TOKEN_PREFIX+token)){

                 return chain.filter(exchange);
             }

         }
         //拦截
        ServerHttpResponse response = exchange.getResponse();
         response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        Result<Object> result = Result.fail(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase(), null);
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] bytes = new byte[0];
        try {
            bytes = objectMapper.writeValueAsBytes(result);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        DataBuffer wrap = response.bufferFactory().wrap(bytes);



        return response.writeWith(Mono.just(wrap));
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
