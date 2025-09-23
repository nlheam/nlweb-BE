package com.nlweb.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationDTO {

    private Long id;
    private String applicationType;
    private UserDTO userDTO;
    private String targetDescription;
    private String status;
    private String errorMessage;
    private LocalDateTime appliedAt;

}
