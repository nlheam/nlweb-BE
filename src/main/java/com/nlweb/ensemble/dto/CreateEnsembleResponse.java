package com.nlweb.ensemble.dto;

import com.nlweb.ensemble.entity.*;
import lombok.*;

@Data
@Builder
public class CreateEnsembleResponse {

    private EnsembleInfo ensembleInfo;

    public static CreateEnsembleResponse fromEntity(Ensemble ensemble, boolean includePrivateInfo) {
        return CreateEnsembleResponse.builder()
                .ensembleInfo(EnsembleInfo.fromEntity(ensemble, includePrivateInfo))
                .build();
    }

}
