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

"""
API 엔드포인트 (수정할 것 같긴 한데 일단 이렇게 적어 놓음)
"""

## Authentication
POST   /api/v1/auth/register              # 회원가입

POST   /api/v1/auth/login                 # 로그인

POST   /api/v1/auth/logout                # 로그아웃

POST   /api/v1/auth/refresh               # 토큰 재발급

GET    /api/v1/auth/verify                # 토큰 검증

## Users (일반 사용자)
GET    /api/v1/users                      # 사용자 목록 조회

GET    /api/v1/users/me                   # 내 정보 조회

PUT    /api/v1/users/me                   # 내 정보 수정

PATCH  /api/v1/users/me                   # 내 정보 부분 수정

DELETE /api/v1/users/me                   # 내 계정 삭제

GET    /api/v1/users/search               # 사용자 검색
?q={query}&session={session}&batch={batch}&page={page}&limit={limit}

GET    /api/v1/users/{user_id}            # 특정 사용자 조회

GET    /api/v1/users/{user_id}/ensembles  # 특정 사용자 참여 합주

GET    /api/v1/users/{user_id}/timeslots  # 특정 사용자 시간표

## Admin
GET    /api/v1/admin/users                # 모든 사용자 조회 (관리자)

GET    /api/v1/admin/users/pending        # 승인 대기 사용자

POST   /api/v1/admin/users/{user_id}/approve   # 사용자 승인

POST   /api/v1/admin/users/{user_id}/reject    # 사용자 거절

POST   /api/v1/admin/users/{user_id}/deactivate # 사용자 비활성화

POST   /api/v1/admin/users/{user_id}/activate   # 사용자 활성화

GET    /api/v1/admin/statistics           # 전체 통계 정보

GET    /api/v1/admin/audit-logs           # 감사 로그

GET    /api/v1/admin/admins               # 관리자 목록

POST   /api/v1/admin/admins/{user_id}     # 관리자 권한 부여

DELETE /api/v1/admin/admins/{user_id}     # 관리자 권한 해제

GET    /api/v1/admin/admins/{user_id}/permissions # 권한 조회

## Ensembles
GET    /api/v1/ensembles                  # 합주 목록 조회
?status={incomplete|completed}&session={session}&page={page}&limit={limit}

POST   /api/v1/ensembles                 # 합주 생성

GET    /api/v1/ensembles/me              # 내 참여 합주

GET    /api/v1/ensembles/search          # 합주 검색
?q={query}&artist={artist}&title={title}&page={page}&limit={limit}

GET    /api/v1/ensembles/{ensemble_id}   # 특정 합주 조회

PUT    /api/v1/ensembles/{ensemble_id}   # 합주 수정

DELETE /api/v1/ensembles/{ensemble_id}   # 합주 삭제

## Ensemble Sessions
GET    /api/v1/ensembles/{ensemble_id}/sessions     # 합주 세션 정보

POST   /api/v1/ensembles/{ensemble_id}/sessions/apply # 세션 신청
Body: {"session_type": "vocal", "user_id": "20250001"}

DELETE /api/v1/ensembles/{ensemble_id}/sessions/{session_type} # 세션 신청 취소

## Timeslots
GET    /api/v1/timeslots                 # 시간표 목록
?date={date}&view={daily|weekly|monthly}&ensemble_id={id}&user_id={id}

POST   /api/v1/timeslots                # 시간표 생성

GET    /api/v1/timeslots/me             # 내 시간표

GET    /api/v1/timeslots/{timeslot_id}  # 특정 시간표 조회

PUT    /api/v1/timeslots/{timeslot_id}  # 시간표 수정

DELETE /api/v1/timeslots/{timeslot_id}  # 시간표 삭제

## Schedule Views
GET    /api/v1/timeslots/schedule        # 일정 조회
?date=2023-10-15&view=daily       # 특정 날짜 일정
?date=2023-10-15&view=weekly      # 주간 일정
?month=2023-10&view=monthly       # 월간 일정

## Events
GET    /api/v1/events                   # 이벤트 목록
?status={upcoming|available|past}&page={page}&limit={limit}

POST   /api/v1/events                  # 이벤트 생성

GET    /api/v1/events/{event_id}       # 특정 이벤트 조회

PUT    /api/v1/events/{event_id}       # 이벤트 수정

DELETE /api/v1/events/{event_id}       # 이벤트 삭제

## Event Participants
GET    /api/v1/events/{event_id}/participants        # 참가자 목록

POST   /api/v1/events/{event_id}/participants        # 참가자 추가
Body: {"user_id": "20250001"}

DELETE /api/v1/events/{event_id}/participants/{user_id} # 참가 취소

## Notifications
GET    /api/v1/notifications            # 내 알림 목록
?status={read|unread}&page={page}&limit={limit}

GET    /api/v1/notifications/unread-count # 읽지 않은 알림 수

PUT    /api/v1/notifications/{notification_id}/read  # 알림 읽음 처리 (POST → PUT)

PUT    /api/v1/notifications/read-all   # 모든 알림 읽음 처리

## Applications
GET    /api/v1/applications             # 신청 목록
?type={ensemble|timeslot|session}&status={pending|approved|rejected}&page={page}

GET    /api/v1/applications/me         # 내 신청 목록

POST   /api/v1/applications/{event_id}/ensembles  # 합주 생성 신청

POST   /api/v1/applications/{ensemble_id}/sessions   # 세션 신청

POST   /api/v1/applications/{ensemble_id}/timeslots  # 시간표 생성 신청

GET    /api/v1/applications/{application_id} # 특정 신청 조회
