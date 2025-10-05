package com.nlweb.admin.dto;

import com.nlweb.user.dto.UserInfo;
import lombok.*;
import java.util.List;

@Data
@Builder
public class UpdateUserStatusResponse {

    private List<UserInfo> successUsers;
    private List<FailedInfo> errors;
    private int totalCount;
    private int successCount;
    private int errorCount;

    @Data
    @AllArgsConstructor
    public static class FailedInfo {
        private String studentId;
        private String errorMessage;
    }
}
