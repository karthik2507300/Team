# CertifyPro Microservices — Build Spec (for service builders)

Every business service follows the **auth-service** template exactly. Read the reference:
`microservices/auth-service/` (fully implemented) and the monolith at `backend/`.

## Package & layout (per service, base = `com.certifypro.<svc>`)
```
<svc>/pom.xml
<svc>/src/main/resources/application.yml       # name + config import ONLY
<svc>/src/main/java/com/certifypro/<svc>/
  <Svc>ServiceApplication.java                 # @SpringBootApplication @EnableDiscoveryClient @EnableFeignClients
  entity/        JPA entities (Lombok @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder)
  common/        enums (copy the module's enums from backend/model/enums)
  repository/    Spring Data repositories
  dto/request/   request records (copy validation annotations from monolith)
  dto/response/  response records + ApiResponse + PageResponse
  service/       interface  (one per aggregate)
  service/impl/  implementation
  controller/    @RestController (same paths & @PreAuthorize as monolith)
  config/        SecurityConfig, RoleBasedHeaderFilter, FeignClientInterceptor
  security/      AuthPrincipal, SecurityUtil, RoleGuard
  aspect/        LoggingAspect  (pointcut package = com.certifypro.<svc>)
  exception/     BusinessException, NotFoundException, GlobalExceptionHandler
  util/          PageUtil
  client/        Feign clients + client/dto (only if the service consumes another)
```

## COPY VERBATIM from auth-service (change only the package `auth` → `<svc>`)
`dto/response/ApiResponse.java`, `dto/response/PageResponse.java`,
`exception/*`, `util/PageUtil.java`,
`security/AuthPrincipal.java`, `security/SecurityUtil.java`, `security/RoleGuard.java`,
`config/RoleBasedHeaderFilter.java`, `config/FeignClientInterceptor.java`,
`aspect/LoggingAspect.java` (update pointcut package string).

## SecurityConfig (per service — NO passwordEncoder bean, NO auth endpoints)
Copy auth-service `config/SecurityConfig.java` but change `permitAll` matchers to:
`"/actuator/**"`, `"/api/**/internal/**"`. Everything else `.authenticated()`.
Keep `@EnableMethodSecurity`, the RoleBasedHeaderFilter wiring, and the JSON 401/403 handlers.

## pom.xml (per service)
Parent = `certifypro-microservices`. Dependencies: `spring-boot-starter-web`,
`-data-jpa`, `-security`, `-validation`, `-aop`, `-actuator`,
`spring-cloud-starter-netflix-eureka-client`, `-config`, `-openfeign`,
`-circuitbreaker-resilience4j`, `mysql-connector-j` (runtime), `lombok` (optional).
Add `com.github.librepdf:openpdf:1.3.43` ONLY to exam-service. NO jjwt (only auth signs tokens).
Include the lombok exclude in spring-boot-maven-plugin (see auth-service pom).

## application.yml (per service) — ONLY these lines
```yaml
spring:
  application:
    name: <svc>            # e.g. candidate-service (must match config-repo/<svc>.yml)
  config:
    import: "optional:configserver:${CONFIG_SERVER_URL:http://localhost:8888}"
  cloud:
    config:
      fail-fast: false
```
(port, datasource, resilience4j come from config-repo/<svc>.yml — already written.)

## Entities & relationships
- Copy field-for-field from `backend/model/*` (same column names). Use Lombok + @Builder.
- Cross-service references stay **plain `Long` id columns** (no JPA relationship, no FK).
- INTRA-service references become real JPA relationships (add these):
  - candidate-service: `ProgramEnrolment` → `@ManyToOne CertificationProgram program` (join col `program_id`);
    `CertificationProgram` → `@OneToMany List<GradingScale>` optional. Candidate stays standalone (userId is a Long).
  - exam-service: `SeatAllocation` → `@ManyToOne ExamWindow`, `@ManyToOne TestCentre`;
    `InvigilatorAssignment` → `@ManyToOne ExamWindow`, `@ManyToOne TestCentre` (userId stays Long).
  - question-service: `QuestionPaper` → `@OneToMany List<PaperQuestion>`; `PaperQuestion` → `@ManyToOne Question`.
  - result-service: `MarksEntry` → `@ManyToOne ScriptAllocation`; `ReEvaluationRequest` → `@ManyToOne CandidateResult`.
  - certificate-service: `RenewalApplication` → `@ManyToOne Certificate`.
  When adding @ManyToOne, keep a readable id via `@JoinColumn(name="...")` and (optional) `insertable=false` mirror
  ONLY if you keep the raw id field; simplest is to replace the raw id field with the relationship and expose the id
  through the response DTO via `entity.getProgram().getProgramId()`.

## Repositories
Use `PagingAndSortingRepository<T,Long>` + `CrudRepository<T,Long>` when only paging/CRUD/derived finders
are needed. Extend `JpaRepository` only when a custom `@Query` / bulk / flush is required (mirror the monolith).

## Cross-cutting simplifications (apply consistently)
1. **Audit log** lives only in auth-service. Business services do NOT write to it — their critical
   actions are captured by the LoggingAspect into `spring.log`. Drop `AuditLogUtil` calls from ported logic.
2. **Pure existence-only FK validation** on foreign ids may be dropped (accept the id). Implement Feign ONLY
   for the flows listed below (they produce user-visible behavior).
3. Keep all `@PreAuthorize` exactly as the monolith controllers had them.

## INTERNAL endpoints each service must EXPOSE (raw DTO, no ApiResponse envelope; path under `/internal`)
- auth-service (DONE): `GET /api/users/internal/{id}` → UserDto; `GET /api/users/internal/by-role/{role}` → List<UserDto>
- candidate-service:
  - `POST /api/candidates/internal/register` body `{userId,name,email,phone,dateOfBirth,gender,highestQualification,professionalExperience,employerName}` → 201 (creates Candidate, status Active)
  - `GET /api/candidates/internal/{candidateId}` → CandidateDto
  - `GET /api/programs/internal/{programId}` → ProgramDto `{programId,programName,level,validityYears,maxAttempts}`
  - `GET /api/programs/internal/{programId}/grading-scale` → List<GradingScaleDto> `{gradeLetter,minPercentage,maxPercentage,isPassing}` (result-service uses this to grade)
- exam-service:
  - `GET /api/exam-windows/internal/{windowId}` → ExamWindowDto
  - `GET /api/seat-allocations/internal/{allocationId}` → SeatAllocationDto `{allocationId,candidateId,windowId,centreId,hallTicketNumber}`
- question-service:
  - `GET /api/question-papers/internal/{paperId}` → PaperDto `{paperId,windowId,programId,totalMarks}`
- certificate-service:
  - `POST /api/certificates/internal/issue` body `{candidateId,programId}` → CertificateDto (auto-issue on result publish)
- notification-service:
  - `POST /api/notifications/internal` body `{userId,message,category}` → 201
  - `POST /api/notifications/internal/by-role` body `{role,message,category}` → 200 (fans out to all users of role)

## Feign clients each service CONSUMES (declare local copies of the foreign DTOs under client/dto)
- exam-service → candidate-service (`CandidateClient`: getProgram, getCandidate — for hall-ticket PDF),
  notification-service (`NotificationClient`: notifyUser on seat confirmation). Guard with @CircuitBreaker + fallback.
- result-service → certificate-service (`CertificateClient`: issue on Pass publish),
  notification-service (`NotificationClient`: notifyUser/notifyRole), question-service (`PaperClient`: getPaper for totalMarks).
  Guard each with @CircuitBreaker (+ fallback that logs and degrades gracefully).
- certificate-service → notification-service (`NotificationClient`: notifyUser on issue).
- notification-service → auth-service (`UserClient`: usersByRole to fan out by-role notifications).
- analytics-service → exam/result/certificate/candidate: implement simple aggregation via Feign to
  existing list endpoints where possible; if data isn't exposed, record 0 for that metric. Keep it runnable.

## Resilience4j
Circuit-breaker instance names in config-repo/<svc>.yml are already set to the DOWNSTREAM service name
(e.g. `candidate-service`, `notification-service`). Use `@CircuitBreaker(name="<downstream>", fallbackMethod=...)`
on the gateway/wrapper method that makes each Feign call (see auth-service `client/CandidateProfileGateway`).

## DataSeeder
Only candidate-service seeds a couple of demo CertificationPrograms (optional, nice-to-have). Others: none.
Do NOT seed users anywhere except auth-service.
