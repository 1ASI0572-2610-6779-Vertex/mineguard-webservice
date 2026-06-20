# MineGuard Platform

MineGuard Platform is a RESTful API for mining-operations safety (fleet monitoring, fatigue
detection, operational alerts and analytics) built with **Domain-Driven Design (DDD)** and
**CQRS**. A single backend serves three clients:

- **mineguard-webapp** — Angular 21 administration/supervision web app.
- **mobile_iot** — Flutter operator/supervisor mobile app.
- **smart-band-edge-service** — Python/Flask IoT edge that synchronizes heart-rate telemetry
  (the platform is the "cloud" the edge syncs to).

It mirrors the architecture and conventions of the ACME Learning Center Platform:
persistence-agnostic domain (pure aggregates + JPA persistence entities + assemblers + repository
adapters), `Result<T, ApplicationError>` application results, centralized `GlobalExceptionHandler`,
OpenAPI docs, i18n, and a snake_case + pluralized table naming strategy.

## Tech Stack

- **Language:** Java 21 (Spring Boot 4 runs on 21; bump to 26 in the IDE if a JDK 26 is installed)
- **Framework:** Spring Boot 4.0.6
- **Persistence:** Spring Data JPA / Hibernate — H2 (dev) / MySQL 8+ (prod)
- **Security:** Spring Security + JWT (jjwt), BCrypt, plus device `X-API-Key` auth for the edge
- **Docs:** SpringDoc OpenAPI (Swagger UI)
- **Build:** Maven

## Bounded Contexts

```
src/main/java/com/mineguard/platform/
├── iam/            # Users, Roles, Supervisors, Devices, JWT auth (web + mobile + edge device)
├── subscriptions/  # Subscription lifecycle, plans, payments and billing REST endpoints
├── monitoring/     # Sensor, SensorReading, Alert, Incident, edge heart-rate ingestion + view models
├── profile/        # Reserved skeleton (4 empty layers — frontends don't consume profiles yet)
├── planning/       # GeofenceZone, ZoneBoundary, ZonePermission (domain only; seeded, no REST)
├── analytics/      # Dashboard/analytics read projections, performance metrics, reports, admin
├── assets/         # Driver, Vehicle, Trip + inventory/directory/catalog view models
└── shared/         # Domain bases, error handling, OpenAPI, i18n, naming strategy
```

> Read-only "view-model" endpoints (dashboards, directories, etc.) are CQRS read projections:
> they are seeded from the shared dataset and exposed verbatim in the exact shape each frontend
> expects (flat arrays, `{notices:[…]}` / `{entries:[…]}` envelopes, snake_case fields where the
> client requires them), so **no frontend code needs to change**.

## Running

Default profile is `dev` (in-memory H2 — no database install required). Data is seeded on startup
from `src/main/resources/seed/db.json`.

```bash
mvn spring-boot:run          # http://localhost:8080
```

Production (MySQL):

```bash
SPRING_PROFILES_ACTIVE=prod DATABASE_URL=localhost DATABASE_NAME=mineguard_platform \
DATABASE_USER=root DATABASE_PASSWORD=password JWT_SECRET=your-long-secret \
mvn spring-boot:run
```

- **Swagger UI:** http://localhost:8080/swagger-ui.html
- **H2 console (dev):** http://localhost:8080/h2-console  (JDBC URL `jdbc:h2:mem:mineguard`)

### Environment variables

| Variable | Description | Default (dev) |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Active profile | `dev` |
| `PORT` | App port | `8080` |
| `DATABASE_URL/PORT/NAME/USER/PASSWORD` | MySQL connection (prod) | localhost/3306/mineguard_platform/root/password |
| `JWT_SECRET` | JWT signing secret | dev default |
| `EDGE_DEFAULT_DEVICE_ID` / `EDGE_DEFAULT_API_KEY` | Seeded smart-band credentials | `smart-band-001` / `test-api-key-123` |

## API Surface (matches the existing frontend contracts)

### IAM
- `POST /authentication/sign-in` → `{id, username, token, role}` (web)
- `POST /authentication/sign-up` → `{id, username}`
- `POST /auth/sign-in` → `{workerId, fullName, role, token}` (mobile, by worker id)
- `GET/POST /supervisors`, `PUT /supervisors/{id}`

### Subscriptions
- `GET /api/v1/plans`
- `GET /api/v1/subscriptions/{id}`
- `GET /api/v1/subscriptions/user/{userId}`
- `POST /api/v1/subscriptions`
- `POST /api/v1/subscriptions/{id}/cancel`
- `POST /api/v1/subscriptions/{id}/renew`
- `PUT /api/v1/subscriptions/{id}/payment-method`
- `POST /api/v1/subscriptions/{id}/payments`
- `POST /api/v1/subscriptions/{id}/payments/{paymentId}/retry`

### Assets
- `GET/POST /vehiclesInventory`, `PUT /vehiclesInventory/{id}` — `operational|maintenance`
- `GET /driversDirectory`, `GET /catalogSummary`
- `GET /vehicles` (mobile) — `available|inUse|maintenance`

### Monitoring
- `GET /operationalAlerts`, `PUT /operationalAlerts/{id}`
- `GET /auditLog` (`{entries:[…]}`), `GET /cardiacReadings`, `GET /fleetSummary`, `GET /liveMapVehicles`
- `GET /alerts` (mobile, `panic|collisionRisk|fatigue`), `POST /alerts/{id}/action`
- **Edge:** `POST /api/v1/health-monitoring/data-records` (header `X-API-Key`, body `{device_id, bpm, created_at?}`)
  → `201 {id, device_id, bpm, created_at}`; raises a fatigue alert when bpm is abnormal (US007).

### Analytics
- `GET /dashboardSummary`, `/dashboardTrend`, `/dashboardRiskDrivers`, `/dashboardRecentAlerts`
- `GET /performanceMetrics`, `/reports`
- `GET /analyticsFatigueBars`, `/analyticsIncidentDistribution`, `/analyticsHistoryRows`, `/analyticsInsights`
- `GET /adminSummary`, `/adminNotices` (`{notices:[…]}`)
- `GET /performance/{workerId}` (mobile)

## Security model

Stateless JWT (`Authorization: Bearer <token>`). Public routes: `/authentication/**`, `/auth/**`,
`/api/v1/health-monitoring/**` (device `X-API-Key` auth), Swagger, H2 console, and the read-only
view-model GET endpoints polled by the frontends. The smart-band edge authenticates per device
(`device_id` + `X-API-Key`) via the IAM anti-corruption layer.

## Seed / demo credentials

- Web admin: `admin_mineguard` / `123456`
- Mobile operators: each driver's operator id (e.g. `OP-8841`) / `123456`
- Edge device: `smart-band-001` / `test-api-key-123`

## Conventions

- **DDD + CQRS:** writes via `CommandService`, reads via `QueryService`.
- **Persistence-agnostic domain:** aggregates carry no JPA; `*PersistenceEntity` + `*PersistenceAssembler`
  + `*RepositoryImpl` adapters bridge to the domain repository ports.
- **i18n:** `Accept-Language` header (`en` default, `es`).
- `subscriptions` now includes the REST API for plan consultation, subscription lifecycle, payment
  processing and payment-method updates; `planning` and `profile` remain reserved/partial.
