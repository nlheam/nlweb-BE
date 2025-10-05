package com.nlweb.user.dto;

import com.nlweb.user.entity.User;
import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UpdateUserResponse {

    private String studentId;
    private String username;
    private String email;
    private String phone;
    private Boolean isVocalable;

    public static UpdateUserResponse from(User user) {
        return UpdateUserResponse.builder()
                .studentId(user.getStudentId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .isVocalable(user.isVocalable())
                .build();
    }

}
