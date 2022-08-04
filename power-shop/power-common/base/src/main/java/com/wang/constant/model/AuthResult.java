package com.wang.constant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResult {

    private String accessToken;

    private Long expiresIn;

    private String type;
}
