package com.nlweb.auth.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;

    private String refreshToken;

    private long expiresIn; // 초 단위

    @Builder.Default
    private String tokenType = "Bearer";

}
