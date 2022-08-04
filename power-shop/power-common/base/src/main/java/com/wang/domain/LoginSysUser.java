package com.wang.domain;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * 专门做登录的类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "sys_user")
public class LoginSysUser implements Serializable, UserDetails {
    @TableId(value = "user_id", type = IdType.AUTO)
    private Long userId;

    /**
     * 用户名
     */
    @TableField(value = "username")
    private String username;

    /**
     * 密码
     */
    @TableField(value = "`password`")
    private String password;

    /**
     * 状态  0：禁用   1：正常
     */
    @TableField(value = "`status`")
    private Integer status;

    /**
     * 用户所在的商城Id
     */
    @TableField(value = "shop_id")
    private Long shopId;



    @TableField(exist = false)
    private Set<String> auths;

    /**
     * 权限集合
     * 因为授权中心 不需要解析
     * 只登录
     * 解析 是在业务模块里面做的 这里不需要用权限
     *
     * @return
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    public String getUsername() {
        return this.username;
    }

    /**
     * 密码不需要存储 解析不用密码
     * 解析只要用户名和权限即可
     *
     * @return
     */
    @JSONField(serialize = false)
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.status == 1;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.status == 1;
    }

    @Override
    public boolean isEnabled() {
        return this.status == 1;
    }

    /**
     * 处理,
     *
     * @param auths
     */
    public void setAuths(Set<String> auths) {
      /*  HashSet<String> rightAuths = new HashSet<>();
        auths.forEach(auth -> {
            if (auth.contains(",")) {
                String[] realAuths = auth.split(",");
                for (String realAuth : realAuths) {
                    rightAuths.add(realAuth);
                }
            } else {
                rightAuths.add(auth);
            }
        });
        this.auths = rightAuths;*/
        HashSet<String> rightAuths = new HashSet<>();
        auths.forEach(auth->{
          if(auth.contains(",")){
              String[] realAuths = auth.split(",");
              for (String realAuth : realAuths) {
                  rightAuths.add(realAuth);
              }

          }else {
              rightAuths.add(auth);
          }
      });
        this.auths = rightAuths;
    }
}