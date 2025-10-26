package com.nlweb.ensemble.dto;

import lombok.*;

@Data
public class CreateEnsembleMemberRequest {

    private Long userId;
    private Long eventId;
    private String session;

}
