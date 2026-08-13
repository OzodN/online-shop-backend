# Task: User Registration Endpoint
**Status:** [x] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Implement the `POST /api/v1/auth/register` endpoint that allows new users to register. This is a public endpoint (no authentication required). On success, it creates a new user with the `CUSTOMER` role and returns the user's public profile.

You must implement the full vertical slice: DTO → Mapper → Service → Controller.

**1. Request DTO** (`user/dto/RegisterRequest.java`):
- Java Record (immutable)
- Fields: `email` (`@NotBlank`, `@Email`), `password` (`@NotBlank`, `@Size(min = 8, max = 100)`), `firstName` (`@NotBlank`, `@Size(max = 100)`), `lastName` (`@NotBlank`, `@Size(max = 100)`), `phone` (nullable, `@Size(max = 20)`)
- Jakarta Bean Validation annotations for field-level validation

**2. Response DTO** (`user/dto/UserResponse.java`):
- Java Record (immutable)
- Fields: `externalId` (UUID), `email`, `firstName`, `lastName`, `phone`, `roles` (`Set<String>`), `createdAt` (Instant)
- This is the public representation of a user — note: NO `id` (Long), NO `passwordHash`, NO `deletedAt`

**3. MapStruct Mapper** (`user/mapper/UserMapper.java`):
- `@Mapper(componentModel = "spring")`
- Method: `UserResponse toResponse(User user)` — maps entity to response DTO
- The `roles` field needs custom mapping: `Set<Role>` → `Set<String>` (extract `Role.getName()`)

**4. AuthService** (`user/service/AuthService.java`):
- `register(RegisterRequest request)` method:
  - Check if email already exists → throw `DuplicateResourceException` (from `common` module)
  - Hash the password using `PasswordEncoder` (Spring Security's `BCryptPasswordEncoder`)
  - Look up the `CUSTOMER` role from `RoleRepository`
  - Create a new `User` entity, assign the role, save to DB
  - Return the mapped `UserResponse`

**5. PasswordEncoder Bean:**
- Register a `BCryptPasswordEncoder` as a `@Bean` — decide where this bean definition should live (think: is it a global security concern or module-specific?)

**6. AuthController** (`user/controller/AuthController.java`):
- `@RestController` with `@RequestMapping("/api/v1/auth")`
- `POST /register` — accepts `@Valid @RequestBody RegisterRequest`, returns `ResponseEntity<UserResponse>` with HTTP 201 Created

> **Think about:**
> - Rule 5 says: Never expose JPA entities from controllers. Your controller returns `UserResponse`, not `User`. ✓
> - Rule 6 says: Only `externalId` (UUID) is exposed. Your `UserResponse` has `externalId`, not `id`. ✓
> - Where should the `PasswordEncoder` bean live? The `app` module's `SecurityConfig` already exists and is where global security beans are configured. Or should it be in `common`? Think about which module will use it.
> - What if the `CUSTOMER` role doesn't exist in the database? Your service should handle this gracefully — this would be a server configuration error, not a user error.
> - The `@Valid` annotation triggers Jakarta Bean Validation. If validation fails, Spring throws `MethodArgumentNotValidException`. Your `GlobalExceptionHandler` (from S2/T2) should already handle this — verify!
> - The password must be hashed BEFORE saving. Never persist plaintext passwords.

## 🧠 Context Files to Read
- `context/architecture/api-design.md` — Section 7.3, Auth & User Module endpoint catalog (`POST /api/v1/auth/register`)
- `context/architecture/flows-and-security.md` — Section 8.1, JWT Flow (registration step)
- `context/ai-workflow-rules.md` — Rule 5 (DTO discipline), Rule 6 (ID exposure), Rule 7 (layered architecture), Rule 8 (error handling)
- `context/code-standards.md` — Section 12.1 (validation strategy)

## ✅ Acceptance Criteria
- [ ] `RegisterRequest.java` is a Java Record with `email`, `password`, `firstName`, `lastName`, `phone` — all with Jakarta validation annotations
- [ ] `UserResponse.java` is a Java Record with `externalId`, `email`, `firstName`, `lastName`, `phone`, `roles`, `createdAt` — no internal `id` or `passwordHash`
- [ ] `UserMapper.java` is a MapStruct `@Mapper` that maps `User` → `UserResponse` with custom `roles` mapping
- [ ] `AuthService.java` has a `register()` method that validates email uniqueness, hashes password, assigns CUSTOMER role, saves user
- [ ] `AuthService` uses `PasswordEncoder` for password hashing (BCrypt)
- [ ] `AuthService` throws `DuplicateResourceException` (from `common`) when email already exists
- [ ] `AuthController.java` has `POST /register` endpoint that returns HTTP 201 with `UserResponse`
- [ ] Controller uses `@Valid` for request validation
- [ ] `PasswordEncoder` bean is registered (in `SecurityConfig` or appropriate config class)
- [ ] `mvn clean compile` passes from the project root
- [ ] `POST /api/v1/auth/register` works end-to-end (test manually with curl or Postman):
  - Successful registration returns 201 + user data
  - Duplicate email returns 409 Conflict (RFC 7807 format)
  - Invalid input returns 400 Bad Request (RFC 7807 format)
