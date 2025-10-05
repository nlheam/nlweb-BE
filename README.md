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

POST   /api/auth/refresh               # 토큰 재발급

GET    /api/auth/verify                # 토큰 검증

### Users (일반 사용자)
GET    /api/users                        # 사용자 목록 조회

POST   /api/users                        # 사용자 생성

GET    /api/users/me                     # 내 정보 조회

PATCH  /api/users/me                     # 내 정보 수정

DELETE /api/users/me                     # 내 계정 삭제

POST   /api/users/me/revive              # 내 계정 복구

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
