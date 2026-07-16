# CertifyPro — Microservices Platform

The CertifyPro monolith (`../backend`) refactored into a Spring Cloud microservices
architecture. Each business module (from the requirements doc) is now an independently
deployable service with its own database, fronted by an API gateway and coordinated
through service discovery and a central config server.

> The original monolith in `../backend` is left intact for reference. This folder
> (`microservices/`) is the new system. The React frontend (`../frontend`) now talks
> only to the API gateway (`http://localhost:8080`).

## Architecture

```
                         ┌────────────────┐
  React app  ─────────►  │  api-gateway    │  :8080   (JWT GlobalFilter → X-Auth-* headers)
  (:5173)                └───────┬─────────┘
                                 │ lb:// (Eureka)
      ┌──────────────┬───────────┼───────────┬──────────────┬───────────────┐
      ▼              ▼           ▼            ▼              ▼               ▼
 auth-service  candidate-svc  exam-svc  question-svc   result-svc   certificate-svc  ...
   :8081          :8082        :8083      :8084          :8085          :8086
      │              │           │            │              │               │
   MySQL          MySQL       MySQL        MySQL          MySQL           MySQL   (db-per-service)

  Supporting:  eureka-server :8761   config-server :8888 (serves ../config-repo)
  Plus:        analytics-service :8087    notification-service :8088
```

### Services

| Service | Port | Module (req. §) | Owns |
|---|---|---|---|
| **config-server** | 8888 | infra | Central config (native, from `config-repo/`) |
| **eureka-server** | 8761 | infra | Service registry |
| **api-gateway** | 8080 | infra | Routing + JWT validation `GlobalFilter` |
| **auth-service** | 8081 | 2.1 IAM | User, AuditLog, JWT issuing, RBAC |
| **candidate-service** | 8082 | 2.2 | Candidate, ProgramEnrolment, CertificationProgram, GradingScale |
| **exam-service** | 8083 | 2.3 | ExamWindow, TestCentre, SeatAllocation, InvigilatorAssignment |
| **question-service** | 8084 | 2.4 | Question, QuestionPaper, PaperQuestion |
| **result-service** | 8085 | 2.5 | ScriptAllocation, MarksEntry, CandidateResult, ReEvaluationRequest |
| **certificate-service** | 8086 | 2.6 | Certificate, RenewalApplication |
| **analytics-service** | 8087 | 2.7 | ExaminationReport |
| **notification-service** | 8088 | 2.8 | Notification |

## How security works (JWT + header propagation)

1. `auth-service` is the **only** JWT issuer (login/register/refresh).
2. The **api-gateway** `JwtAuthenticationGlobalFilter` validates the token on every
   request (except `/api/auth/login|register|refresh-token`), then injects trusted
   identity headers downstream: `X-Auth-User-Id`, `X-Auth-User-Email`, `X-Auth-Role`
   (and strips any client-supplied copies to prevent spoofing).
3. Each service's **`RoleBasedHeaderFilter`** rebuilds the Spring `SecurityContext`
   from those headers, so `@PreAuthorize("hasRole('Admin')")` etc. work unchanged.
4. **`FeignClientInterceptor`** forwards the same headers on inter-service Feign calls,
   so authorization flows through the whole call chain.

## Cross-cutting concerns (per requirements)

- **Service discovery** — Netflix Eureka (`eureka-server`).
- **API gateway** — Spring Cloud Gateway with a `GlobalFilter` validating JWTs.
- **Config server** — Spring Cloud Config in `native` mode, reading the local
  `config-repo/` folder. `application.yml` there is shared by all services.
- **Feign** — inter-service calls (e.g. auth→candidate at registration, result→certificate
  auto-issue, *→notification). Local DTO copies per service.
- **Resilience4j** — every Feign call is wrapped in a `@CircuitBreaker` gateway component
  with a graceful fallback (see e.g. `auth-service/.../client/CandidateProfileGateway.java`).
- **Aspect logging** — each service has `aspect/LoggingAspect` logging entry/exit/timing
  of all controller & service methods; output goes to that service's `logs/spring.log`
  (configured centrally via `logging.file.name`).
- **Lombok + @Builder** on all entities; **records** for DTOs.
- **JPA relationships** are used *within* a service (e.g. `QuestionPaper`→`PaperQuestion`,
  `MarksEntry`→`ScriptAllocation`); references that cross a service boundary are plain
  `Long` id columns + Feign.
- **Repositories** extend `PagingAndSortingRepository`/`CrudRepository` where that suffices,
  and only `JpaRepository` where a custom `@Query` or bulk op is needed.

## Prerequisites

- JDK 21+
- MySQL running on `localhost:3306` (user/pass default `root`/`root` — override via env).
  Each service auto-creates its own schema (`certifypro_auth`, `certifypro_candidate`, …).

## Run order (each in its own terminal, from `microservices/`)

Start infrastructure first, then the business services (any order):

```bash
# 1. Config server (must be first — others fetch config from it)
./mvnw -pl config-server spring-boot:run

# 2. Eureka registry
./mvnw -pl eureka-server spring-boot:run

# 3. API gateway
./mvnw -pl api-gateway spring-boot:run

# 4. Business services
./mvnw -pl auth-service spring-boot:run
./mvnw -pl candidate-service spring-boot:run
./mvnw -pl exam-service spring-boot:run
./mvnw -pl question-service spring-boot:run
./mvnw -pl result-service spring-boot:run
./mvnw -pl certificate-service spring-boot:run
./mvnw -pl analytics-service spring-boot:run
./mvnw -pl notification-service spring-boot:run
```

Build everything at once: `./mvnw clean install -DskipTests`
Eureka dashboard: <http://localhost:8761> · Frontend: `cd ../frontend && npm install && npm run dev`

### Overriding config (env vars, optional)
`DB_USERNAME`, `DB_PASSWORD`, `<SVC>_DB_URL`, `JWT_SECRET`, `EUREKA_URL`, `CONFIG_SERVER_URL`.

## Demo accounts (seeded by auth-service on startup)
Password for all: **`Password@123`** — `admin@`, `centreadmin@`, `examcontroller@`,
`evaluator@`, `certofficer@`, `candidate@` `certifypro.com`.

## Decomposition trade-offs (deliberate, documented)

The monolith shared one DB with cross-module foreign keys and a shared transaction.
Splitting into DB-per-service required these pragmatic changes:

1. **Audit log is centralized in auth-service.** Business services no longer write to a
   shared `audit_log`; their critical actions are captured by the `LoggingAspect` into
   `spring.log`. (Central AuditLog remains for user administration.)
2. **Registration is a saga-lite.** `auth-service` creates the `User`, then calls
   `candidate-service` to create the profile (circuit-breaker guarded). If candidate-service
   is down, the account is still created and the profile can be completed later.
3. **Pure existence-only FK validations were dropped** across service boundaries; only
   cross-service calls that produce user-visible behavior are implemented via Feign.
4. **`result-service.compute()`** now derives candidates from its local script/marks data
   (grouped by candidate) instead of the seat roster (which lives in exam-service), and
   auto-issues certificates via `certificate-service` instead of writing them locally.
5. **`analytics-service`** aggregates only what existing internal endpoints expose; metrics
   requiring cross-service aggregate queries are returned as `0` with an explanatory `note`
   (full aggregation endpoints are a Phase-2 item).

See `SERVICE_SPEC.md` for the exact per-service contracts and internal endpoints.
