package com.nlweb.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import com.nlweb.common.enums.UserStatus;
import com.nlweb.user.entity.User;
import com.nlweb.user.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterResponse {

    private UserInfo userInfo;

    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime registeredAt;

    public static RegisterResponse fromEntity(User user) {
        return RegisterResponse.builder()
                .userInfo(UserInfo.fromEntity(user, true))
                .message("회원가입이 성공적으로 완료되었습니다. 관리자 승인 후 로그인할 수 있습니다.")
                .registeredAt(user.getCreatedAt())
                .build();
    }
}
