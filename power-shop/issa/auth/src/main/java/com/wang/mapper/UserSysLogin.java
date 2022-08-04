package com.wang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wang.domain.LoginSysUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.Set;

@Mapper
public interface UserSysLogin extends BaseMapper<LoginSysUser> {


    Set<String> selectPermsBySysUserId(Long userId);

}
