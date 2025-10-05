package com.nlweb.user.dto;

import com.nlweb.user.entity.User;
import lombok.Data;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@Builder
public class CreateUserResponse {

    private String studentId;
    private String username;
    private LocalDateTime createdAt;

    public static CreateUserResponse from(User user) {
        return CreateUserResponse.builder()
                .studentId(user.getStudentId())
                .username(user.getUsername())
                .createdAt(user.getCreatedAt())
                .build();
    }

}
