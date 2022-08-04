package com.wang.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 网关授权常量
 */
public interface AuthConstant {
    /**
     *网关放行
     */
        List<String>  ALLOE_URL = Arrays.asList("/auth-server/doLogin");

    /**
     * token的key
     */
    String AUTHORIZATION ="Authorization";

    /**
     * BEARER持票人
     */

    String BEARER ="bear ";
    /**
     * redis 的token
     */
    String TOKEN_PREFIX = "loginToken:";

    String LOGIN_URL="/login";

    String LOGIN_OUT="/logout";

    String LOGIN_TYPE = "loginType";
    /**
     * 后台管理员类型
     */
    String SYS_USER_TYPE = "sys_user";

    /**
     * 前台会员类型
     */
    String MEMBER_TYPE = "member";
    Long TOKEN_EXPIRE_TIME = 7200L;

    Long TOKEN_EXPIRE_THRESHOLD = 300L;
    /**
     * 请求头里面的header
     */
    String GLOBAL_TOKEN_HEADER = "myGlobalToken";



}
