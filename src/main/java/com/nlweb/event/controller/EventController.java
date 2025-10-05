package com.nlweb.event.controller;

import com.nlweb.event.dto.*;
import com.nlweb.common.dto.*;
import com.nlweb.event.service.EventService;
import com.nlweb.common.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

}
