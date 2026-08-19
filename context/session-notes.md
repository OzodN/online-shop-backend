# Session Notes

> Detailed log of each working session — context needed to resume work.
> Updated after every session. Read the latest entry to understand where we left off.

---

**Session 1 (2026-07-13):** 
- Completed system design brainstorming and created comprehensive system design document. Decomposed the design into modular knowledge base context files. No code has been written yet.

**Session 2 (2026-07-15):** 
- Assembled the virtual AI team (5 agents: Architecture Guardian, Code Quality Reviewer, Security Analyst, DBA Reviewer, QA Testing Coach). Created sprint map for Phase 1 (5 sprints, ~22 tasks). Generated detailed task files for Sprint 1 (4 tasks). Ready to begin coding T1: Initialize Maven Multi-Module Project.

**Session 3 (2026-07-25):** 
- Completed T1: Initialize Maven Multi-Module Project. Code review by Architecture Guardian and Code Quality Reviewer identified: (1) main class was in wrong package (`dev.ozodn.onlineshop.app` → moved to `dev.ozodn.onlineshop`), (2) groupId inconsistency across modules (unified to `dev.ozodn.onlineshop` for all modules), (3) missing dependency management entries (added Lombok, Testcontainers BOM, PostgreSQL driver, Flyway), (4) decided on Spring Boot 3.5.x over 4.x due to ecosystem compatibility, (5) Lombok 1.18.36→1.18.38 to fix `ExceptionInInitializerError: TypeTag :: UNKNOWN` incompatibility with JDK 21. All issues resolved. `mvn clean compile` passes. Ready for T2: Docker Compose + PostgreSQL.

**Session 4 (2026-08-02):** 
- Completed T2: Set Up Docker Compose with PostgreSQL. Developer created `docker-compose.yaml`, `init.sql` (4 schemas), and `application.yaml`. Architecture Guardian review requested 3 changes: (1) `.yml` → `.yaml` naming — developer chose `.yaml` as project standard (recorded as AD #13), (2) missing JPA dialect — added `org.hibernate.dialect.PostgreSQLDialect`, (3) `public` in Flyway schemas list — removed. All review changes applied. Docker verified: PostgreSQL 16 starts, 4 schemas created, Spring Boot connects successfully. All 7/7 acceptance criteria met.

**Session 5 (2026-08-03):** 
- Completed T3: Configure Spring Boot, Spring Security & Spring Modulith. Developer implemented: (1) `SecurityConfig` with CSRF disabled, stateless sessions, and permitAll — placed in `app` module since global security wiring belongs where all modules are visible, (2) Spring Modulith BOM in parent POM (`v1.3.1`), `spring-modulith-starter-core` and `spring-modulith-starter-test` in `app/pom.xml`, (3) `ApplicationModulesTest` with `modules.verify()`. Spring runs successfully, modulith test passes. All 8/8 acceptance criteria met. 
- Completed T4: Gitignore and Full Verification. `.gitignore` created with Java/Maven/IntelliJ patterns; fixed two issues during review (removed erroneous `sprints/` ignore, added `application-local.yaml`). All 9/9 acceptance criteria met. **Sprint S1 — Project Foundation is now complete.** Ready for Sprint S2 — Common Module.

**Session 6 (2026-08-10):**
- Completed S2/T1: Implement `BaseEntity` and `SoftDeletableEntity`. Initial review by Architecture Guardian, Code Quality Reviewer, and DBA Reviewer identified 4 issues: (1) missing `@Id` annotation on primary key field — critical JPA bug, (2) `BaseEntity` not declared `abstract` — violated reference design, (3) unused validation imports (`Constraint`, `NotNull`, `ConstraintComposition`), (4) wildcard imports (`jakarta.persistence.*`, `lombok.*`). Developer fixed issues 1-3; issue 4 accepted as non-blocking nit. Re-review: all 3 reviewers approved. All 11/11 acceptance criteria met. `mvn clean compile` passes.
- Completed S2/T2: Implement Global Exception Handler (RFC 7807). Three review rounds: Round 1 identified 6 issues (BaseException not abstract, localhost:8080 URIs, AccessDeniedException protected constructor + naming collision with Spring Security, ConstraintViolation info leakage, formatting). Round 2 fixed most but caught wrong import (`java.nio.file.AccessDeniedException` instead of Spring Security's) and missed BaseException URI. Round 3: all fixed, all 3 reviewers approved. All 10/10 acceptance criteria met.
- Completed S2/T3: Implement Shared Pagination DTOs. Two review rounds: Round 1 identified 4 issues (JetBrains `@Contract`/`@NotNull` annotations — unnecessary dependency, inconsistent null-safety mixing Lombok + JetBrains, Russian test display names, missing edge case tests). Developer fixed all: removed JetBrains annotations, kept `@NonNull` only on parameters, translated tests to English, added empty page + null guard tests. All 7/7 acceptance criteria met. Ready for S2/T4.

**Session 7 (2026-08-13):**
- Completed S2/T4: Configure JPA Auditing. Developer implemented `JpaAuditingConfig` in `common/config/` with `@EnableJpaAuditing(auditorAwareRef = "auditorAware")` and a private static inner class `SpringSecurityAuditorAware` implementing `AuditorAware<String>`. Review approved on first round — clean implementation with proper three-way null guard (null auth, unauthenticated, anonymous token) and `"system"` fallback. One improvement made after review: added `.filter(name -> !name.isBlank()).or(() -> Optional.of("system"))` chain to guarantee non-null audit trail even when `getName()` returns null or blank. All 7/7 acceptance criteria met.
- **Sprint S2 — Common Module is now complete** (all 4 tasks done: BaseEntity, Global Exception Handler, Pagination DTOs, JPA Auditing). Ready for Sprint S3 — User Registration & Auth.

**Session 8 (2026-08-19):**
- Completed S3/T1: Flyway Migrations for User Schema. Developer created `V1__create_user_schema_tables.sql` with `users`, `roles`, `user_roles`, and `refresh_tokens` tables in `user_schema`. Learning discussion on how Flyway location scanning works in multi-module Maven projects (classpath merging, single history table, version uniqueness). Developer chose flat `classpath:db/migration` approach with globally unique version numbers — simple and practical for a solo project. Added `locations: classpath:db/migration` to `application.yaml`. Good defensive SQL: `CREATE TABLE IF NOT EXISTS`, `ON CONFLICT (name) DO NOTHING` for role seeding. Review approved on first round — all 9/9 acceptance criteria met. Ready for S3/T2.
