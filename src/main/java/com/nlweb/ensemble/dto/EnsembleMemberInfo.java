package com.nlweb.ensemble.dto;

import com.nlweb.common.enums.EnsembleSessionType;
import com.nlweb.user.dto.UserInfo;
import com.nlweb.ensemble.entity.EnsembleMember;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnsembleMemberInfo {

    private UserInfo userInfo;
    private EnsembleSessionType ensembleSessionType;

    public static EnsembleMemberInfo fromEntity(EnsembleMember member, boolean includePrivateInfo) {
        return EnsembleMemberInfo.builder()
                .userInfo(UserInfo.fromEntity(member.getUser(), includePrivateInfo))
                .ensembleSessionType(member.getSession())
                .build();
    }
}
