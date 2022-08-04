package com.wang.utils;

import com.wang.constant.ResourceConstant;

import java.util.ArrayList;
import java.util.List;

public class PermitUtils {
        public static String[] allPermit(){
            List<String> resourceAllowUrls = ResourceConstant.RESOURCE_ALLOW_URLS;
            List<String> businessAllowUrls = ResourceConstant.BUSINESS_ALLOW_URLS;
            ArrayList<String> strings = new ArrayList<>();
            resourceAllowUrls.forEach(r->strings.add(r));
            businessAllowUrls.forEach(r->strings.add(r));
            String[] allArr = new String[strings.size()];
            for (int i = 0; i < strings.size(); i++) {
                allArr[i] = strings.get(i);
            }
            return allArr;


        }
}
