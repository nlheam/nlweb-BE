package com.nlweb.ensemble.dto;

import com.nlweb.ensemble.entity.*;
import lombok.*;

@Data
@Builder
public class CreateEnsembleMemberResponse {

    private EnsembleMemberInfo ensembleMemberInfo;

    public static CreateEnsembleMemberResponse fromEntity(EnsembleMember ensembleMember, boolean includePrivateInfo) {
        return CreateEnsembleMemberResponse.builder()
                .ensembleMemberInfo(EnsembleMemberInfo.fromEntity(ensembleMember, includePrivateInfo))
                .build();
    }

}
