# 지온이네 가족 (JioniFamily)

엄마, 아빠, 지온이 3명이 사용하는 가족 미션/보상 커뮤니케이션 앱.
부모가 미션을 등록하면 지온이가 수행하고, 부모가 승인하면 코인 보상을 받는 시스템.

## 스크린샷

| 로그인 | 부모 홈 | 지온이 홈 |
|--------|---------|-----------|
| 아바타 선택 + PIN 입력 | 미션 목록 + 승인 대기 | 미션 완료 + 코인 현황 |

| 미션 만들기 | 지난 미션 | 설정 |
|------------|----------|------|
| 이름/설명/코인/카테고리/반복 | 주별 히스토리 + 요약 | PIN 변경/로그아웃 |

## 기술 스택

| 레이어 | 기술 |
|--------|------|
| Android | Kotlin + Jetpack Compose + Hilt + Retrofit + DataStore |
| Backend | Python 3.9+ + FastAPI + SQLAlchemy async + aiosqlite |
| DB | SQLite (개발) / PostgreSQL (프로덕션) |
| Auth | JWT (7일) + 4자리 PIN (bcrypt) |
| Deploy | Railway (backend) / Google Play (Android) |

## 프로젝트 구조

```
JioniFamily/
├── backend/
│   ├── app/
│   │   ├── main.py              # FastAPI app + lifespan (DB init, auto-seed)
│   │   ├── config.py            # Pydantic Settings
│   │   ├── api/
│   │   │   ├── deps.py          # get_db, get_current_user, require_parent
│   │   │   └── v1/              # auth, users, missions, completions, stats
│   │   ├── models/              # SQLAlchemy (family, user, mission, mission_completion)
│   │   ├── schemas/             # Pydantic v2 request/response
│   │   ├── services/            # Business logic (week계산, 미션, 완료, 통계)
│   │   ├── core/                # security.py (JWT, PIN), exceptions.py
│   │   └── db/                  # session.py, seed.py
│   └── requirements.txt
│
└── android/
    └── app/src/main/java/com/jionifamily/
        ├── di/                  # Hilt modules (AppModule, RepositoryModule)
        ├── data/
        │   ├── remote/          # Retrofit API interfaces, DTOs, AuthInterceptor
        │   ├── local/           # DataStoreManager
        │   └── repository/      # Repository 구현체
        ├── domain/
        │   ├── model/           # Mission, MissionCompletion, User, WeeklyStats
        │   └── repository/      # Repository 인터페이스
        ├── presentation/
        │   ├── theme/           # 파스텔 컬러, 타이포그래피
        │   ├── navigation/      # Screen, JioniNavGraph (역할별 라우팅)
        │   ├── common/components/  # MissionCard, StatusChip
        │   ├── splash/          # SplashScreen + ViewModel
        │   ├── login/           # 아바타 선택 + PIN 입력
        │   ├── parent/          # 홈 (승인대기/미션목록), 미션 만들기
        │   ├── child/           # 홈 (코인/진행률/완료버튼)
        │   └── shared/          # 히스토리, 설정 (PIN변경/로그아웃)
        └── util/                # Result, WeekUtils
```

## 개발 환경 설정

### Backend

```bash
cd backend
python3 -m pip install -r requirements.txt
python3 -m app.main    # http://localhost:8000 서버 시작 (자동 시드)
```

시드 데이터:
- 가족: 지온이네 가족
- 엄마 (PIN: 1234), 아빠 (PIN: 5678), 지온이 (PIN: 0000)
- 샘플 미션 3개 (수학 공부하기, 방 청소하기, 줄넘기 100회)

### Android

```bash
cd android
./gradlew assembleDebug    # APK 빌드
```

에뮬레이터 연결:
```bash
adb reverse tcp:8000 tcp:8000    # 에뮬레이터 → 호스트 포트 포워딩
```

## API 엔드포인트

Base path: `/api/v1/`

| Method | Path | 설명 |
|--------|------|------|
| POST | `/auth/login` | PIN 로그인 (JWT 발급) |
| POST | `/auth/refresh` | 토큰 갱신 |
| PUT | `/auth/pin` | PIN 변경 |
| GET | `/users/me` | 내 정보 |
| GET | `/users/family` | 가족 구성원 |
| GET | `/missions` | 미션 목록 (주간 completion 자동 생성) |
| POST | `/missions` | 미션 생성 (부모 전용) |
| PUT | `/missions/{id}` | 미션 수정 |
| DELETE | `/missions/{id}` | 미션 삭제 (soft delete) |
| GET | `/missions/completions` | 주간 완료 현황 |
| POST | `/missions/{id}/submit` | 미션 완료 제출 (자녀) |
| POST | `/completions/{id}/approve` | 미션 승인 + 코인 적립 (부모) |
| POST | `/completions/{id}/reject` | 미션 거절 (부모) |
| GET | `/stats/weekly` | 주간 통계 |
| GET | `/stats/history` | 과거 주간 히스토리 |

## 핵심 설계

### 주간 사이클
- **일요일 시작** (Asia/Seoul 기준)
- 홈 화면에 `📅 4/12 (일) ~ 4/18 (토)` 형태로 기간 표시

### 미션 반복
- **반복 미션** (on/off): 매주 자동으로 새 completion 레코드 생성
- **일회성 미션**: 생성된 주에만 completion 존재

### 주간 리셋
- 새 주 시작 시, 과거 주의 미승인 미션 → `missed` (미완료) 자동 처리
- `pending`, `submitted` 상태 모두 → `missed`

### 상태 플로우
```
pending → submitted → approved (코인 적립)
                    → rejected
pending/submitted → missed (주간 마감 시 자동)
```

### 역할별 화면
- **부모**: 홈 (승인대기/미션목록) / 미션 만들기 / 지난 미션 / 설정
- **자녀**: 홈 (코인/진행률/완료버튼) / 지난 미션 / 설정

## 디자인

파스텔 컬러 팔레트:

| 색상 | Hex | 용도 |
|------|-----|------|
| Pastel Pink | `#FFB5C2` | Primary, 엄마 액센트 |
| Pastel Blue | `#B5D8FF` | 아빠 액센트 |
| Pastel Mint | `#B5F5D8` | 지온이, 성공/승인 |
| Pastel Yellow | `#FFF5B5` | 코인/보상 |
| Cream White | `#FFF8F0` | 배경 |

## 라이선스

Private project.
