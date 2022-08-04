package com.wang.config;


import com.fasterxml.jackson.databind.ObjectMapper;
;
import com.wang.constant.model.Result;
import com.wang.filter.TokenTransferFilter;
import com.wang.utils.PermitUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;

/**
 * 这是我们的资源服务器配置
 * 每个业务模块 都依赖我 它们都是资源服务器
 * 访问时必须携带token
 * 我们在登录之前 搞一个过滤器 tokenTranslateFilter (token转换过滤器)
 * 在这个tokenTranslateFilter 里面将token转成用户对象 放在securityContextHolder去
 * 再将这个过滤器 放在usernamePasswordAuthenticationFilter之前 那么security会认为你已经登陆了
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private TokenTransferFilter tokenTransferFilter;

    /**
     * 这个是资源服务器的http请求配置
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关闭跨站请求伪造
        http.csrf().disable();
        // 给资源服务器写一个访问被拒的处理器 403
        http.exceptionHandling().accessDeniedHandler(accessDeniedHandler());
        // 我们的token转换过滤器放在认证过滤器之前  即可跳过登陆
        http.addFilterBefore(tokenTransferFilter, UsernamePasswordAuthenticationFilter.class);
        // 配置资源放行接口
        http.authorizeRequests()
                .antMatchers(PermitUtils.allPermit()) // 放行的接口
                .permitAll()
                .anyRequest()
                .authenticated();
    }

    /**
     * 如果权限不够 则报403
     *
     * @return
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setCharacterEncoding("UTF-8");
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            Result<String> result = Result.fail(HttpStatus.FORBIDDEN.value(), "权限不够");
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(result);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.flush();
            writer.close();
        };
    }
}
