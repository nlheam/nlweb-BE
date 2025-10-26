# nlWeb Backend API

## 🚀 Quick Start with Docker

**Docker hub에 이미지 업로드함**
```bash
cd docker && docker compose up -d
```

** Docker 에러 발생하면
```bash
docker compose down -v
docker compose up -d
```

### 테스트용 관리자
- ID: 12345678
- username: test-admin
- password: test-admin


## API 엔드포인트
### Authentication
POST   /api/auth/register              # 회원가입

POST   /api/auth/login                 # 로그인

POST   /api/auth/logout                # 로그아웃

POST   /api/auth/change-password       # 비밀번호 변경

POST   /api/auth/reset-password        # 비밀번호 재설정 (이메일 발송) - 아직 구현 안 함

POST   /api/auth/tokens                # 토큰 재발급

GET    /api/auth/verify                # 토큰 검증

### Users (일반 사용자)
GET    /api/users                        # 사용자 목록 조회

POST   /api/users                        # 사용자 생성

GET    /api/users/me                     # 내 정보 조회

PATCH  /api/users/me                     # 내 정보 수정

DELETE /api/users/me                     # 내 계정 삭제

POST   /api/users/me/restoration         # 내 계정 복구

GET    /api/users/{studentId}            # 특정 사용자 조회

GET    /api/users/search                 # 사용자 검색   
?q={query}&session={session}&batch={batch}&page={page}&limit={limit}

GET    /api/users/sessions/{session}     # 세션별 사용자 조회

GET    /api/users/batch/{batch}          # 기수별 사용자 조회

GET    /api/users/statistics             # 사용자 통계

### Admins (집부 관리자)
GET    /api/admins                              # 관리자 목록

POST   /api/admins                              # 관리자 권한 부여

DELETE /api/admins/{studentId}                    # 관리자 권한 해제

GET    /api/admins/users/pending                # 승인 대기 사용자

PATCH  /api/admins/users/status     # 사용자 상태 변경

### Events (이벤트)
POST   /api/events                             # 이벤트 생성

GET    /api/events                             # 이벤트 목록 조회

GET    /api/events/{eventId}                   # 특정 이벤트 조회

GET    /api/events/active                      # 활성화된 이벤트 조회

GET    /api/events/ongoing                     # 진행 중인 이벤트 조회

GET    /api/events/upcoming                    # 예정된 이벤트 조회

GET    /api/events/past                        # 지난 이벤트 조회

GET    /api/events/{eventType}                 # 타입별 이벤트 조회

PATCH  /api/events/{eventId}                   # 이벤트 수정 (body: {status: "active" | "inactive"})

PATCH  /api/events/{eventId}                   # 이벤트 상태 수정

[//]: # (PATCH  /api/events/{eventId}/activate          # 이벤트 활성화)

[//]: # (PATCH  /api/events/{eventId}/deactivate        # 이벤트 비활성화)

DELETE /api/events/{eventId}                   # 이벤트 삭제

GET    /api/events/{eventId}/participants      # 이벤트 참가자 목록

POST   /api/events/{eventId}/participants      # 이벤트 참가자 생성

DELETE /api/events/{eventId}/participants/{studentId} # 이벤트 참가자 삭제

POST   /api/events/{eventId}/apply             # 이벤트 참가 신청

DELETE /api/events/{eventId}/cancel            # 이벤트 참가 신청 취소

GET    /api/events/{eventId}/me                # 내가 참가한 이벤트 조회
