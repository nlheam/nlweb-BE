package com.nlweb.dto.response.auth;

import com.nlweb.dto.UserDTO;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;

    private String refreshToken;

    private UserDTO userInfo;

    private long expiresIn; // 초 단위

    private String tokenType; // "Bearer"

}
