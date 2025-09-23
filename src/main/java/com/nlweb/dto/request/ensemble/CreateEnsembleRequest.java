package com.nlweb.dto.request.ensemble;

import lombok.*;
import jakarta.validation.constraints.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateEnsembleRequest {

    @NotNull(message = "이벤트 ID는 필수입니다")
    private Long eventId;

    @NotBlank(message = "아티스트명은 필수입니다")
    @Size(min = 1, max = 200, message = "아티스트명은 1자 이상 200자 이하여야 합니다")
    private String artist;

    @NotBlank(message = "곡명은 필수입니다")
    @Size(min = 1, max = 200, message = "곡명은 1자 이상 200자 이하여야 합니다")
    private String title;

    @Min(value = 1, message = "난이도는 1 이상이어야 합니다")
    @Max(value = 5, message = "난이도는 5 이하여야 합니다")
    private Integer difficultyLevel;

    @Min(value = 1, message = "연주 시간은 1분 이상이어야 합니다")
    private Integer estimatedDuration;

    @Size(max = 100, message = "장르는 100자를 초과할 수 없습니다")
    private String genre;

    @Size(max = 1000, message = "메모는 1000자를 초과할 수 없습니다")
    private String notes;

    /**
     * 초기 멤버 배정 (악기 -> 학번)
     */
    private Map<String, String> initialMembers;
}
