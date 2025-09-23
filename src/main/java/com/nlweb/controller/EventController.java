package com.nlweb.controller;

import com.nlweb.dto.request.event.CreateEventRequest;
import com.nlweb.dto.request.event.UpdateEventRequest;
import com.nlweb.dto.request.user.UpdateUserRequest;
import com.nlweb.dto.response.ApiResponse;
import com.nlweb.dto.response.PageResponse;
import com.nlweb.dto.UserDTO;
import com.nlweb.dto.EventDTO;
import com.nlweb.enums.UserSessionType;
import com.nlweb.enums.UserStatus;
import com.nlweb.service.UserService;
import com.nlweb.service.EventService;
import com.nlweb.validation.groups.ValidationGroups;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@Tag(name = "이벤트 API", description = "합주스터디 / 세션스터디 / 공연 / 행사 관련 API")
@Slf4j
@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    @Operation(summary = "이벤트 목록", description = "모든 활성 이벤트 목록 조회")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventDTO>>> getActiveEvents() {
        List<EventDTO> events = eventService.getActiveEvents();
        return ResponseEntity.ok(ApiResponse.success(events));
    }

    /**
     * 이벤트 생성
     */
    @Operation(summary = "이벤트 생성", description = "새 이벤트 생성 (관리자만)")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventDTO>> createEvent(
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        String createdBy = principal.getUsername();
        EventDTO event = eventService.createEvent(request, createdBy);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(event, "이벤트가 생성되었습니다"));
    }

    /**
     * 이벤트 수정
     */
    @Operation(summary = "이벤트 수정", description = "이벤트 정보 수정")
    @PutMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<EventDTO>> updateEvent(
            @PathVariable Long eventId,
            @Valid @RequestBody CreateEventRequest request,
            @AuthenticationPrincipal UserDetails principal) {

        String updatedBy = principal.getUsername();
        EventDTO event = eventService.updateEvent(eventId, request, updatedBy);
        return ResponseEntity.ok(ApiResponse.success(event, "이벤트가 수정되었습니다"));
    }

    /**
     * 이벤트 삭제 (비활성화)
     */
    @Operation(summary = "이벤트 삭제", description = "이벤트 삭제 (비활성화)")
    @DeleteMapping("/{eventId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @PathVariable Long eventId,
            @AuthenticationPrincipal UserDetails principal) {

        String deletedBy = principal.getUsername();
        eventService.deleteEvent(eventId, deletedBy);
        return ResponseEntity.ok(ApiResponse.success(null, "이벤트가 삭제되었습니다"));
    }

}
