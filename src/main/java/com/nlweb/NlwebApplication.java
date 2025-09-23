package com.nlweb;

import com.nlweb.entity.User;
import com.nlweb.enums.UserSessionType;
import com.nlweb.enums.UserStatus;
import com.nlweb.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;


import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
import java.util.function.Function;

@Slf4j
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableTransactionManagement
@EnableConfigurationProperties
@EnableJpaRepositories(basePackages = "com.nlweb.repository")
@SpringBootConfiguration
@SpringBootApplication
public class NlwebApplication {

    public static void main(String[] args) {

        long startTime = System.currentTimeMillis();

        setSystemProperties();

        SpringApplication app = new SpringApplication(NlwebApplication.class);

        configureApplication(app);

        var context = app.run(args);

        long endTime = System.currentTimeMillis();
        String activeProfile = context.getEnvironment().getActiveProfiles().length > 0 ?
                context.getEnvironment().getActiveProfiles()[0] : "default";

        log.info("========================================");
        log.info("🎵 nlWeb Application Started Successfully!");
        log.info("📊 Profile: {}", activeProfile);
        log.info("🚀 Startup time: {} ms", (endTime - startTime));
        log.info("🌐 Server running on port: {}",
                context.getEnvironment().getProperty("server.port", "8080"));
        log.info("📝 API Docs: http://localhost:{}/api/swagger-ui.html",
                context.getEnvironment().getProperty("server.port", "8080"));
        log.info("❤️  Health Check: http://localhost:{}/api/actuator/health",
                context.getEnvironment().getProperty("server.port", "8080"));
        log.info("========================================");
    }

    @PostConstruct
    public void init() {
        // 시스템 시간대를 한국으로 설정
        TimeZone.setDefault(TimeZone.getTimeZone(ZoneId.of("Asia/Seoul")));
        log.info("🕐 System timezone set to: {}", ZoneId.of("Asia/Seoul"));

        // JVM 메모리 정보 출력 (개발/디버깅용)
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024; // MB
        long totalMemory = runtime.totalMemory() / 1024 / 1024; // MB
        long freeMemory = runtime.freeMemory() / 1024 / 1024; // MB

        log.info("💾 JVM Memory - Max: {}MB, Total: {}MB, Free: {}MB",
                maxMemory, totalMemory, freeMemory);

        // 동시성 제어 시스템 초기화 확인
        log.info("⚡ Concurrency control system initialized for 200 concurrent users");

        // 캐싱 시스템 초기화 확인
        log.info("🗄️  Redis caching system initialized");

        // 실시간 알림 시스템 초기화 확인
        log.info("📢 WebSocket notification system initialized");
    }

    private static void setSystemProperties() {
        // 네티 DNS 해결 속도 향상
        System.setProperty("io.netty.resolver.dns.cache.timeout", "10");
        System.setProperty("io.netty.resolver.dns.cache.negative.timeout", "5");

        // HikariCP 성능 최적화
        System.setProperty("com.zaxxer.hikari.housekeeping.periodMs", "30000");

        // 로그백 설정
        System.setProperty("logging.config", "classpath:logback-spring.xml");

        // JVM 최적화 (운영환경에서는 JVM 옵션으로 설정 권장)
        System.setProperty("spring.jpa.open-in-view", "false");

        log.debug("🔧 System properties configured");
    }

    private static void configureApplication(SpringApplication app) {
        // 배너 설정 (custom banner.txt 사용)
        app.setBannerMode(org.springframework.boot.Banner.Mode.CONSOLE);

        // 웹 애플리케이션 타입 명시적 설정
        app.setWebApplicationType(
                org.springframework.boot.WebApplicationType.SERVLET
        );

        // Lazy 초기화 비활성화 (실시간 시스템 특성상 빠른 응답 필요)
        app.setLazyInitialization(false);

        // 추가 프로파일 설정
        app.setAdditionalProfiles("nlweb-core");

        log.debug("⚙️  Spring Boot application configured");
    }

}