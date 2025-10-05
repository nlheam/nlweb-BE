package com.nlweb.admin.dto;

import com.nlweb.user.dto.UserInfo;
import lombok.*;
import java.util.List;

@Data
@Builder
public class ApproveUsersResponse {

    private List<UserInfo> approvedUsers;
    private List<FailedInfo> errors;
    private int totalCount;
    private int approvedCount;
    private int errorCount;

    @Data
    @AllArgsConstructor
    public static class FailedInfo {
        private String studentId;
        private String errorMessage;
    }

}
