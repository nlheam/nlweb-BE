package com.nlweb.event.controller;

import com.nlweb.common.enums.EventType;
import com.nlweb.event.dto.*;
import com.nlweb.common.dto.*;
import com.nlweb.event.service.EventParticipantService;
import com.nlweb.event.service.EventService;
import com.nlweb.common.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final EventParticipantService eventParticipantService;

    /** 이벤트 생성 */
    @Operation(summary = "이벤트 생성", description = "새로운 이벤트를 생성합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<CreateEventResponse>> createEvent(
            @Valid @RequestBody CreateEventRequest createEventRequest,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String createdBy = principal.getUsername();
        CreateEventResponse response = eventService.createEvent(createdBy, createEventRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 모든 이벤트 조회 */
    @Operation(summary = "모든 이벤트 조회", description = "모든 이벤트를 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<EventInfo>>> getAllEvents() {
        List<EventInfo> response = eventService.getAllEvents();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 이벤트 ID로 이벤트 조회 */
    @Operation(summary = "이벤트 ID로 이벤트 조회", description = "이벤트 ID로 특정 이벤트를 조회합니다.")
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse<EventInfo>> getEventById(
            @PathVariable("eventId") Long eventId) {
        EventInfo response = eventService.getEventById(eventId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 모든 활성화 이벤트 조회 */
    @Operation(summary = "모든 활성화 이벤트 조회", description = "활성화된 모든 이벤트를 조회합니다.")
    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<EventInfo>>> getAllActiveEvents() {
        List<EventInfo> response = eventService.getAllActiveEvents();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 모든 진행 중인 이벤트 조회 */
    @Operation(summary = "모든 진행 중인 이벤트 조회", description = "진행 중인 모든 이벤트를 조회합니다.")
    @GetMapping("/ongoing")
    public ResponseEntity<ApiResponse<List<EventInfo>>> getAllOngoingEvents() {
        List<EventInfo> response = eventService.getAllOngoingEvents();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 모든 다가오는 이벤트 조회 */
    @Operation(summary = "모든 다가오는 이벤트 조회", description = "다가오는 모든 이벤트를 조회합니다.")
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<EventInfo>>> getAllUpcomingEvents() {
        List<EventInfo> response = eventService.getAllUpcomingEvents();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 모든 기간이 끝난 이벤트 조회 */
    @Operation(summary = "모든 기간이 끝난 이벤트 조회", description = "기간이 끝난 모든 이벤트를 조회합니다.")
    @GetMapping("/past")
    public ResponseEntity<ApiResponse<List<EventInfo>>> getAllPastEvents() {
        List<EventInfo> response = eventService.getAllPastEvents();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 이벤트 타입별 모든 활성화 이벤트 조회 */
    @Operation(summary = "이벤트 타입별 모든 활성화 이벤트 조회", description = "이벤트 타입별로 활성화된 모든 이벤트를 조회합니다.")
    @GetMapping("/{eventType}")
    public ResponseEntity<ApiResponse<List<EventInfo>>> getAllEventsByEventType(
            @PathVariable("eventType") String eventType) {
        List<EventInfo> response = eventService.getAllEventsByType(EventType.valueOf(eventType));
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 이벤트 수정 */
    @Operation(summary = "이벤트 수정", description = "기존 이벤트를 수정합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{eventId}")
    public ResponseEntity<ApiResponse<UpdateEventResponse>> updateEvent(
            @PathVariable("eventId") Long eventId,
            @Valid @RequestBody UpdateEventRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String updatedBy = principal.getUsername();
        UpdateEventResponse response = eventService.updateEvent(eventId, request, updatedBy);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 이벤트 활성화 */
    @Operation(summary = "이벤트 활성화", description = "비활성화된 이벤트를 활성화합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{eventId}/activate")
    public ResponseEntity<ApiResponse<Void>> activateEvent(
            @PathVariable("eventId") Long eventId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String activatedBy = principal.getUsername();
        eventService.activateEvent(eventId, activatedBy);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /** 이벤트 비활성화 */
    @Operation(summary = "이벤트 비활성화", description = "활성화된 이벤트를 비활성화합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{eventId}/deactivate")
    public ResponseEntity<ApiResponse<Void>> deactivateEvent(
            @PathVariable("eventId") Long eventId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String deactivatedBy = principal.getUsername();
        eventService.deactivateEvent(eventId, deactivatedBy);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /** 이벤트 삭제 */
    @Operation(summary = "이벤트 삭제", description = "기존 이벤트를 삭제합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(
            @PathVariable("eventId") Long eventId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String deletedBy = principal.getUsername();
        eventService.deleteEvent(eventId, deletedBy);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /** 이벤트 ID로 모든 참가자 조회 */
    @Operation(summary = "이벤트 ID로 모든 참가자 조회", description = "이벤트 ID로 모든 참가자를 조회합니다.")
    @GetMapping("/{eventId}/participants")
    public ResponseEntity<ApiResponse<List<EventParticipantInfo>>> getAllParticipantsByEventId(
            @PathVariable("eventId") Long eventId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        List<EventParticipantInfo> response = eventParticipantService.getAllParticipantsByEventId(eventId, principal.isAdmin());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 이벤트 참가자 생성 */
    @Operation(summary = "이벤트 참가자 생성", description = "기존 사용자를 이벤트 참가자로 등록합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{eventId}/participants")
    public ResponseEntity<ApiResponse<CreateEventParticipantResponse>> createEventParticipant(
            @PathVariable("eventId") Long eventId,
            @Valid @RequestBody CreateEventParticipantRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String createdBy = principal.getUsername();
        CreateEventParticipantResponse response = eventParticipantService.createEventParticipant(createdBy, eventId, request, principal.isAdmin());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 이벤트 참가자 삭제 */
    @Operation(summary = "이벤트 참가자 삭제", description = "이벤트 참가자를 삭제합니다.")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{eventId}/participants/{studentId}")
    public ResponseEntity<ApiResponse<Void>> deleteEventParticipant(
            @PathVariable("eventId") Long eventId,
            @PathVariable("studentId") String studentId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String deletedBy = principal.getUsername();
        eventParticipantService.deleteEventParticipant(eventId, studentId, deletedBy);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /** 이벤트 참가 신청 (사용자 본인) */
    @Operation(summary = "이벤트 참가 신청 (사용자 본인)", description = "사용자가 본인의 학번으로 이벤트 참가를 신청합니다.")
    @PostMapping("/{eventId}/participants/me")
    public ResponseEntity<ApiResponse<CreateEventParticipantResponse>> applyEvent (
            @PathVariable("eventId") Long eventId,
            @Valid @RequestBody CreateEventParticipantRequest request,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String studentId = principal.getUsername();
        CreateEventParticipantResponse response = eventParticipantService.createEventParticipant(studentId, eventId, request, true);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /** 이벤트 참가 신청 취소 (사용자 본인) */
    @Operation(summary = "이벤트 참가 신청 취소 (사용자 본인)", description = "사용자가 본인의 학번으로 이벤트 참가 신청을 취소합니다.")
    @DeleteMapping("/{eventId}/participants/me")
    public ResponseEntity<ApiResponse<Void>> cancleEventApplication (
            @PathVariable("eventId") Long eventId,
            @AuthenticationPrincipal CustomUserDetails principal) {
        String studentId = principal.getUsername();
        eventParticipantService.deleteEventParticipant(eventId, studentId, studentId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /** 내가 참여 중인 모든 이벤트 조회 */
    @Operation(summary = "내가 참여 중인 모든 이벤트 조회", description = "내가 참여 중인 이벤트들을 조회합니다.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<EventInfo>>> getAllMyEvents(
            @AuthenticationPrincipal CustomUserDetails principal) {
        List<EventInfo> response = eventService.getAllEventsByStudentId(principal.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
