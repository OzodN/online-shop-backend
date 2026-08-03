# Task: Configure JPA Auditing
**Status:** [x] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Enable Spring Data JPA Auditing so that `BaseEntity`'s `createdAt`, `updatedAt`, `createdBy`, and `updatedBy` fields are automatically populated whenever entities are persisted or updated.

You must implement:

**1. JPA Auditing Configuration class:**
- Create a `@Configuration` class annotated with `@EnableJpaAuditing`
- Place it in the `common` module (think about the right package — consider `config/`)

**2. `AuditorAware<String>` implementation:**
- The `createdBy` / `updatedBy` fields need to know **who** is performing the action
- Implement `AuditorAware<String>` — a Spring interface that tells JPA auditing what value to use for `@CreatedBy` and `@LastModifiedBy`
- For now, it should extract the current user's identifier from the Spring Security context (`SecurityContextHolder.getContext().getAuthentication()`)
- If no authentication exists (e.g., during system operations or startup), return a fallback value like `"system"`
- Register this as a `@Bean` in your auditing configuration
- The `@EnableJpaAuditing` annotation needs to reference this bean: `@EnableJpaAuditing(auditorAwareRef = "auditorAware")`

**Package location:** `common/src/main/java/dev/ozodn/onlineshop/common/config/`

> **Think about:**
> - What does `SecurityContextHolder.getContext().getAuthentication().getName()` return?
> - What happens if `getAuthentication()` returns null? (e.g., during Flyway migrations or batch operations)
> - Should you return `Optional.empty()` or `Optional.of("system")` when there's no authenticated user? What's the difference?
> - Later, when JWT auth is implemented, the principal will be the user's email or UUID — this `AuditorAware` will automatically pick it up.

## 🧠 Context Files to Read
- `context/ai-workflow-rules.md` — Rule 11 (auditing: JPA auditing MUST be enabled, all entities extend BaseEntity with audit fields)
- `context/architecture/database-and-entities.md` — Section 5.1 (BaseEntity with `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`)
- `context/code-standards.md` — Section 3.4 (module structure — decide where `config/` fits)

## ✅ Acceptance Criteria
- [ ] A `@Configuration` class exists in `common/.../config/` with `@EnableJpaAuditing`
- [ ] An `AuditorAware<String>` implementation exists and is registered as a Spring Bean
- [ ] The `AuditorAware` extracts the current user from `SecurityContextHolder` when authenticated
- [ ] The `AuditorAware` returns a safe fallback (e.g., `"system"`) when no authentication exists
- [ ] `@EnableJpaAuditing` references the `AuditorAware` bean via `auditorAwareRef`
- [ ] `mvn clean compile` passes from the project root
- [ ] The Spring Boot application starts without auditing-related errors
