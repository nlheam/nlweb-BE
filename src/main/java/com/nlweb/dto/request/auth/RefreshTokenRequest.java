package com.nlweb.dto.request.auth;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

/**
 * 토큰 갱신 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshTokenRequest {

    @NotBlank(message = "Refresh Token이 필요합니다")
    private String refreshToken;
}
