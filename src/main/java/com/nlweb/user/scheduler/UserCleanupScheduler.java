package com.nlweb.user.scheduler;

import com.nlweb.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCleanupScheduler {

    private final UserService userService;

    @Scheduled(cron = "0 0 2 * * *")
    public void cleanupSoftDeletedUsers() {
        log.info("6개월 경과 소프트 삭제 사용자 영구 삭제 작업 시작");

        int deletedCount = userService.deleteUserHard();

        log.info("6개월 경과 소프트 삭제 사용자 영구 삭제 작업 완료 - 총 {}명 삭제", deletedCount);
    }

}
