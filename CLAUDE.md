# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

JioniFamily (지온이네 가족) - 가족 미션/보상 커뮤니케이션 시스템.
엄마, 아빠가 지온이에게 미션을 등록하면, 지온이가 수행 후 부모가 승인하여 코인을 적립하는 Android 앱.

- **Backend**: Python 3.12, FastAPI, SQLAlchemy async, SQLite (dev) / PostgreSQL (prod)
- **Android**: Kotlin, Jetpack Compose, Hilt, Retrofit, DataStore
- **Auth**: JWT + 4자리 PIN (bcrypt)
- **Deploy**: Railway (backend), Google Play (Android)

## Development Commands

```bash
# Backend
cd backend
python3 -m pip install -r requirements.txt
python3 -m app.main                    # Run dev server (port 8000)
python3 -m app.db.seed                 # Seed initial data (family + 3 users)
alembic upgrade head                   # Run migrations
alembic revision --autogenerate -m ""  # Create migration
pytest                                 # Run tests

# Android (open in Android Studio)
# Build: ./gradlew assembleDebug
# Test:  ./gradlew testDebugUnitTest
```

## Architecture

### Backend (`backend/`)
- `app/main.py` — FastAPI app with lifespan (DB init + auto-seed)
- `app/config.py` — Pydantic Settings (DATABASE_URL, JWT_SECRET, etc.)
- `app/models/` — SQLAlchemy: family, user, mission, mission_completion
- `app/schemas/` — Pydantic v2 request/response DTOs
- `app/services/` — Business logic (auth, mission, completion, week calculation)
- `app/api/deps.py` — DI: get_db, get_current_user, require_parent
- `app/api/v1/` — Route modules: auth, users, missions, completions, stats
- `app/core/security.py` — JWT encode/decode, PIN hashing (bcrypt)
- `app/db/seed.py` — Seeds family "지온이네 가족" + 3 users (엄마/아빠/지온이)

### Android (`android/`)
- MVVM + Clean Architecture (data → domain → presentation)
- `data/remote/` — Retrofit APIs + AuthInterceptor
- `domain/model/` — User, Mission, MissionCompletion, enums
- `domain/usecase/` — Business logic per feature
- `presentation/theme/` — Pastel color palette, Korean typography
- `presentation/navigation/` — Role-based NavGraph (parent vs child)
- `presentation/{parent,child,shared}/` — Screen composables + ViewModels

## Key Design Decisions

- **Weekly cycle**: ISO 8601 Monday start, timezone Asia/Seoul
- **Recurring missions**: Template pattern. Completion records lazy-created on weekly query
- **Coin atomicity**: SELECT FOR UPDATE in transaction for balance changes
- **Auto-login**: Encrypted DataStore stores JWT + avatar_key
- **Role routing**: NavGraph checks role → separate bottom nav per role
- **Users are pre-seeded**: No registration flow. 3 fixed family members

## API Base Path

`/api/v1/` — Auth, Users, Missions, Completions, Stats

## Color Palette

| Name | Hex | Usage |
|------|-----|-------|
| Pastel Pink | #FFB5C2 | Primary, 엄마 |
| Pastel Blue | #B5D8FF | 아빠, info |
| Pastel Mint | #B5F5D8 | 지온이, success |
| Pastel Yellow | #FFF5B5 | 코인 |
| Cream White | #FFF8F0 | Background |
