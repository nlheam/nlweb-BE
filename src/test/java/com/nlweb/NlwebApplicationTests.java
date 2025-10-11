package com.nlweb;

import com.nlweb.admin.entity.Admin;
import com.nlweb.admin.repository.AdminRepository;
import com.nlweb.common.enums.UserSessionType;
import com.nlweb.common.enums.UserStatus;
import com.nlweb.common.security.CustomUserDetails;
import com.nlweb.common.security.WithMockCustomUser;
import com.nlweb.config.TestRedisConfig;
import com.nlweb.config.TestSecurityConfig;
import com.nlweb.user.entity.User;
import com.nlweb.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestSecurityConfig.class)
@ContextConfiguration(initializers = TestRedisConfig.class)
class NlwebApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** 각 테스트 실행 전에 테스트용 더미 사용자 10명을 생성 */
    @BeforeEach
    void setUp() {
        // 기존 데이터 정리 (Admin이 User를 참조하므로 Admin 먼저 삭제)
        adminRepository.deleteAll();
        userRepository.deleteAll();

        // 테스트용 더미 사용자 10명 생성
        List<User> testUsers = new ArrayList<>();

        // 1. 일반 사용자 (25010001~25010007)
        for (int i = 1; i <= 7; i++) {
            String studentId = String.format("2501000%d", i);
            testUsers.add(User.builder()
                    .studentId(studentId)
                    .username("테스트사용자" + i)
                    .password(passwordEncoder.encode("1234"))
                    .email(studentId + "@test.com")
                    .phone("010-0000-000" + i)
                    .batch(38)
                    .session(UserSessionType.values()[i % UserSessionType.values().length])
                    .status(UserStatus.ACTIVE)
                    .build());
        }

        // 2. 관리자 사용자 (00000001~00000003)
        List<User> adminUsers = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            String studentId = String.format("%08d", i);
            User admin = User.builder()
                    .studentId(studentId)
                    .username("관리자" + i)
                    .password(passwordEncoder.encode("admin1234"))
                    .email("admin" + i + "@test.com")
                    .phone("010-9999-999" + i)
                    .batch(35)
                    .session(UserSessionType.VOCAL)
                    .status(UserStatus.ACTIVE)
                    .build();
            admin.setIsAdmin(true);
            testUsers.add(admin);
            adminUsers.add(admin);
        }

        // 모든 사용자 저장
        userRepository.saveAll(testUsers);

        // Admin 엔티티 생성 (User가 먼저 저장되어야 함)
        List<Admin> admins = new ArrayList<>();
        for (int i = 0; i < adminUsers.size(); i++) {
            User adminUser = adminUsers.get(i);
            String[] roles = {"SUPER_ADMIN", "EVENT_MANAGER", "MEMBER_MANAGER"};
            admins.add(Admin.builder()
                    .user(adminUser)
                    .role(roles[i])
                    .appointedBy("SYSTEM")
                    .appointmentReason("테스트용 관리자 " + (i + 1))
                    .build());
        }
        adminRepository.saveAll(admins);
    }

    @Test
    @WithMockCustomUser
    void testWithAnnotation_DefaultUser() {
        // Given: @WithMockCustomUser로 자동 인증된 사용자

        // When: 현재 인증된 사용자 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        // Then: 기본값 확인 (WithMockCustomUser의 기본값)
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("25010001");
        assertThat(userDetails.getRealUsername()).isEqualTo("테스트사용자");
        assertThat(userDetails.hasRole("USER")).isTrue();
        assertThat(userDetails.hasRole("ADMIN")).isFalse();
    }

    @Test
    @WithMockCustomUser(
            studentId = "00000001",
            username = "관리자",
            roles = {"USER", "ADMIN"},
            isAdmin = true
    )
    void testWithAnnotation_Admin() {
        // Given: @WithMockCustomUser로 관리자 권한을 가진 사용자 인증

        // When: 현재 인증된 사용자 정보 가져오기
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();

        // Then: 관리자 권한 확인
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("00000001");
        assertThat(userDetails.getRealUsername()).isEqualTo("관리자");
        assertThat(userDetails.hasRole("USER")).isTrue();
        assertThat(userDetails.hasRole("ADMIN")).isTrue();
    }

    // ============ 엔드포인트 테스트 ============ //

    @Test
    @WithMockCustomUser
    void testUserEndpoint_GetMyInfo() throws Exception {
        // Given: 일반 사용자로 인증됨

        // When: 내 정보 조회 API 호출
        // Then: 200 OK 응답
        mockMvc.perform(get("/users/me"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockCustomUser
    void testUserEndpoint_GetActiveUsers() throws Exception {
        // Given: 일반 사용자로 인증됨

        // When: 활성 사용자 목록 조회 API 호출
        // Then: 200 OK 응답
        mockMvc.perform(get("/users"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockCustomUser(studentId = "25010001", username = "일반사용자")
    void testUserEndpoint_GetUserInfo() throws Exception {
        // Given: 일반 사용자로 인증됨

        // When: 특정 사용자 정보 조회 API 호출
        // Then: 200 OK 응답
        mockMvc.perform(get("/users/25010001"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockCustomUser(roles = {"USER"})
    void testUserEndpoint_GetStatistics_Forbidden() throws Exception {
        // Given: ADMIN 권한이 없는 일반 사용자로 인증됨

        // When: 사용자 통계 조회 API 호출 (관리자 전용)
        // Then: 403 Forbidden 응답
        mockMvc.perform(get("/users/statistics"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockCustomUser(
            studentId = "00000001",
            username = "nlheam",
            roles = {"USER", "ADMIN"},
            isAdmin = true
    )
    void testAdminEndpoint_GetStatistics() throws Exception {
        // Given: ADMIN 권한을 가진 사용자로 인증됨

        // When: 사용자 통계 조회 API 호출
        // Then: 200 OK 응답
        mockMvc.perform(get("/users/statistics"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    @WithMockCustomUser(
            studentId = "00000001",
            username = "관리자",
            roles = {"USER", "ADMIN"},
            isAdmin = true
    )
    void testAdminEndpoint_GetAllAdmins() throws Exception {
        // Given: ADMIN 권한을 가진 사용자로 인증됨

        // When: 모든 관리자 조회 API 호출
        // Then: 200 OK 응답
        mockMvc.perform(get("/admins"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockCustomUser(
            studentId = "00000001",
            username = "관리자",
            roles = {"USER", "ADMIN"},
            isAdmin = true
    )
    void testAdminEndpoint_GetPendingUsers() throws Exception {
        // Given: ADMIN 권한을 가진 사용자로 인증됨

        // When: 승인 대기 사용자 목록 조회 API 호출
        // Then: 200 OK 응답
        mockMvc.perform(get("/admins/users/pending"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockCustomUser(roles = {"USER"})
    void testAdminEndpoint_GetPendingUsers_Forbidden() throws Exception {
        // Given: ADMIN 권한이 없는 일반 사용자로 인증됨

        // When: 승인 대기 사용자 목록 조회 API 호출 (관리자 전용)
        // Then: 403 Forbidden 응답
        mockMvc.perform(get("/admins/users/pending"))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

}
