package com.nlweb.dto.response.auth;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;

    private String refreshToken;

    private long expiresIn; // 초 단위

    private String tokenType; // "Bearer"

}
