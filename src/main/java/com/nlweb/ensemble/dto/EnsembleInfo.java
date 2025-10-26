package com.nlweb.ensemble.dto;

import com.nlweb.ensemble.entity.*;
import com.nlweb.event.entity.Event;
import lombok.*;
import java.util.List;

@Data
@Builder
public class EnsembleInfo {

    private Event event;
    private String artist;
    private String title;
    private String notes;
    private List<EnsembleMemberInfo> members;
    private Boolean isActive;

    public static EnsembleInfo fromEntity(Ensemble ensemble, boolean includePrivateInfo) {
        return EnsembleInfo.builder()
                .event(ensemble.getEvent())
                .artist(ensemble.getArtist())
                .title(ensemble.getTitle())
                .notes(ensemble.getNotes())
                .members(ensemble.getMembers().stream()
                        .map(member -> EnsembleMemberInfo.fromEntity(member, includePrivateInfo))
                        .toList())
                .isActive(ensemble.getIsActive())
                .build();
    }

}
