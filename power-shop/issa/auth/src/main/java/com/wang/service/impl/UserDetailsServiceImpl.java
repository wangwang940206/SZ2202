package com.wang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.wang.constant.AuthConstant;
import com.wang.domain.LoginSysUser;
import com.wang.ex.LoginException;
import com.wang.mapper.UserSysLogin;
import lombok.SneakyThrows;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {
         @Autowired
         private UserSysLogin userSysLogin;


    @SneakyThrows
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String loginType = request.getHeader(AuthConstant.LOGIN_TYPE);
        if(!StringUtils.hasText(loginType)){
            throw  new  LoginException("登录类型不配");
        }
        switch (loginType){
            case AuthConstant.SYS_USER_TYPE:
                LoginSysUser loginSysUser = userSysLogin.selectOne(new LambdaQueryWrapper<LoginSysUser>()
                        .eq(LoginSysUser::getUsername, username));
                if(!ObjectUtils.isEmpty(loginSysUser)){

                    Set<String> auths = userSysLogin.selectPermsBySysUserId(loginSysUser.getUserId());

                    if(!CollectionUtils.isEmpty(auths)){
                        loginSysUser.setAuths(auths);

                    }
                }
                return loginSysUser;

            case AuthConstant.MEMBER_TYPE:





        }



        return null;
    }
}
