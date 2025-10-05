package com.nlweb.admin.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.util.List;

@Data
public class UpdateUserStatusRequest {

    private List<String> studentIds;

    @NotNull
    @Pattern(regexp = "approve|reject|activate|decativate|suspend", message = "유효하지 않은 action 값입니다.")
    private String action;

    private String reason;

}
