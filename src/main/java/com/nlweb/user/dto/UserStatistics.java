package com.nlweb.user.dto;

import lombok.Data;
import lombok.Builder;
import java.util.List;


@Data
@Builder
public class UserStatistics {

    private long totalUsers;
    private long activeUsers;
    private long pendingUsers;
    private List<Object[]> sessionStatistics;
    private List<Object[]> batchStatistics;
}
