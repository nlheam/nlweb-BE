package com.nlweb.user.dto;

import com.nlweb.user.entity.User;
import lombok.*;

@Data
@Builder
public class DeleteUserResponse {

    private String studentId;
    private String username;
    private String message;

    public static DeleteUserResponse from(User user, String message) {
        return DeleteUserResponse.builder()
                .studentId(user.getStudentId())
                .username(user.getUsername())
                .message(message)
                .build();
    }

}
