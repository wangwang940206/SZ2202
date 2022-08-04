package com.wang.constant;

import java.util.Arrays;
import java.util.List;

/**
 * 放行的接口 比如 swagger  druid 监控  自定义
 */
public interface ResourceConstant {

    List<String> RESOURCE_ALLOW_URLS = Arrays.asList(
            "/v2/api-docs",  // swagger  druid ...
            "/v3/api-docs",
            "/swagger-resources/configuration/ui",  //用来获取支持的动作
            "/swagger-resources",                   //用来获取api-docs的URI
            "/swagger-resources/configuration/security",//安全选项
            "/webjars/**",
            "/swagger-ui/**",
            "/druid/**",
            "/actuator/**"
    );

    /**
     * 业务放行路径
     */
    List<String> BUSINESS_ALLOW_URLS = Arrays.asList("/b");


//
//    /**
//     * 自己机房服务器的ip
//     */
//    List<String> SERVER_IPS = Arrays.asList("127.0.0.1", "192.168.137.1");


}
