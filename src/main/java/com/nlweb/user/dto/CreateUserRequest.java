package com.nlweb.user.dto;

import com.nlweb.auth.dto.RegisterRequest;
import com.nlweb.common.enums.UserSessionType;
import lombok.*;

@Data
@Builder
public class CreateUserRequest {

    private String studentId;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer batch;
    private UserSessionType session;

    public static CreateUserRequest from(RegisterRequest request) {
        return CreateUserRequest.builder()
                .studentId(request.getStudentId())
                .username(request.getUsername())
                .password(request.getPassword())
                .email(request.getEmail())
                .phone(request.getPhone())
                .batch(request.getBatch())
                .session(UserSessionType.valueOf(request.getSession()))
                .build();
    }
}
