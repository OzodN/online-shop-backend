# Task: Configure Spring Boot 4, Spring Security & Spring Modulith
**Status:** [x] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Configure the three core frameworks that will govern the entire application: Spring Boot 4 (base framework), Spring Security (JWT auth foundation), and Spring Modulith (module boundary enforcement).

You must set up:

**Spring Security (initial config):**
- Create a `SecurityConfig` class in the `common` module (or `app` module — your choice, but think about where it logically belongs).
- For now, configure a **permissive security filter chain** that allows all requests. We'll lock it down in Sprint 3 when we implement JWT auth.
- Disable CSRF (we're building a stateless REST API).
- Set session management to `STATELESS`.
- The goal is: the app starts without Spring Security blocking every request with a 401.

**Spring Modulith:**
- Add Spring Modulith dependencies to the parent POM's `<dependencyManagement>` (use the Spring Modulith BOM).
- Add `spring-modulith-starter-core` to the `app` module.
- Add `spring-modulith-starter-test` to the `app` module's test dependencies.
- Verify that Spring Modulith recognizes your module structure by checking application startup logs.

**Application startup verification:**
- The Spring Boot application must start successfully with `mvn spring-boot:run` from the `app` directory (or `mvn -pl app spring-boot:run` from root).
- The app connects to PostgreSQL, Spring Security is active (but permissive), and Spring Modulith detects the modules.

## 🧠 Context Files to Read
- `context/architecture/flows-and-security.md` — Spring Security and JWT flow overview
- `context/architecture/high-level-and-modules.md` — Module structure that Modulith must detect
- `context/ai-workflow-rules.md` — Rule 4: Spring Modulith architecture tests

## ✅ Acceptance Criteria
- [ ] Spring Security `SecurityFilterChain` bean is defined with CSRF disabled and stateless sessions
- [ ] All endpoints are accessible without authentication (temporary — will be locked down in Sprint 3)
- [ ] Spring Modulith BOM is in parent POM's `<dependencyManagement>`
- [ ] `spring-modulith-starter-core` is a dependency in `app/pom.xml`
- [ ] `spring-modulith-starter-test` is a test dependency in `app/pom.xml`
- [ ] Application starts successfully with `mvn spring-boot:run` from the `app` module
- [ ] No errors in startup logs related to Security or Modulith
- [ ] `mvn clean compile` passes from project root
