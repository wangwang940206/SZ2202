package com.wang.utils;

import org.springframework.util.AntPathMatcher;

import java.util.List;

public class PathMacthUtils {
             public static Boolean isMatch(List<String> allowPaths,String realPath){
                 AntPathMatcher antPathMatcher = new AntPathMatcher();
                 for (String allowPath : allowPaths) {
                     boolean flag = antPathMatcher.match(allowPath, realPath);
                     if(flag){
                         return true;
                     }

                 }

                return false;
             }
}
