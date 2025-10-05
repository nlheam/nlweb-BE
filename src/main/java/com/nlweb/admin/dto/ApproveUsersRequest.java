package com.nlweb.admin.dto;

import lombok.*;
import java.util.List;

@Data
public class ApproveUsersRequest {

    private List<String> studentIds;
    private String approvedBy;
    private String reason;

}
