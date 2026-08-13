# Task: JWT Token Service
**Status:** [x] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Implement the JWT token generation and validation service that powers the stateless authentication system. This service creates access tokens (short-lived) and refresh tokens (long-lived), and validates incoming tokens for the security filter chain.

You must implement:

**1. Add the `jjwt` dependency** to the appropriate POM file:
- `io.jsonwebtoken:jjwt-api` (compile)
- `io.jsonwebtoken:jjwt-impl` (runtime)
- `io.jsonwebtoken:jjwt-jackson` (runtime)
- Add version management in the parent POM's `<dependencyManagement>` and `<properties>` sections
- Add the dependency to the module that will use it (think: `user` module? `common`? `app`?)

**2. JWT configuration properties:**
- Add JWT configuration to `application.yaml`:
  - `app.jwt.secret` — a Base64-encoded secret key (at least 256 bits for HS256)
  - `app.jwt.access-token-expiration-ms` — access token TTL (e.g., 900000 = 15 minutes)
  - `app.jwt.refresh-token-expiration-ms` — refresh token TTL (e.g., 604800000 = 7 days)
- Create a `@ConfigurationProperties` class to bind these properties (think about which module it belongs to)

**3. `JwtService`** (`user/service/JwtService.java`):
- `String generateAccessToken(User user)` — creates a JWT with:
  - Subject: user's email (or external ID — think about which is better for the `sub` claim)
  - Claim `roles`: list of user's role names
  - Issued at: now
  - Expiration: now + access token TTL
  - Signed with HMAC-SHA256 using the secret key
- `String generateRefreshToken(User user)` — creates a random UUID string (NOT a JWT) and persists it in the `refresh_tokens` table with the user and expiration time
- `String extractUsername(String token)` — extracts the subject from a JWT
- `boolean isTokenValid(String token)` — validates signature, expiration, and structure
- `Claims extractAllClaims(String token)` — parses and returns all JWT claims

> **Think about:**
> - Access tokens are JWTs — stateless, verified by signature, contain user info in claims
> - Refresh tokens are opaque strings stored in the database — they're looked up, not decoded. Why the difference? (Hint: refresh tokens need to be revocable)
> - The JWT secret MUST be long enough for HS256 (at least 32 bytes / 256 bits). A short secret will cause a runtime `WeakKeyException`.
> - What should the `sub` (subject) claim contain? Email? External ID (UUID)? Think about what your `AuditorAware` uses and what the JWT filter will need to look up the user.
> - Should the `JwtService` be in the `user` module or `common`? JWT handling is specific to user authentication, so `user` makes sense. The JWT filter (T5) will be in `app` and will call this service.
> - For the `@ConfigurationProperties` class — you need `@EnableConfigurationProperties` somewhere or `@ConfigurationPropertiesScan`. Think about where.
> - Never log JWT secrets or tokens at INFO level.

## 🧠 Context Files to Read
- `context/architecture/flows-and-security.md` — Section 8.1 (JWT flow: access token 15min, refresh token 7d, token structure)
- `context/architecture/api-design.md` — Section 7.1 (Auth Header: `Authorization: Bearer <access-token>`)
- `context/ai-workflow-rules.md` — Rule 7 (layered architecture — service layer), Rule 12 (base package)
- `context/code-standards.md` — Section 12.3 (logging — never log secrets at INFO)

## ✅ Acceptance Criteria
- [ ] `jjwt` dependencies (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) are added to the parent POM's `<dependencyManagement>` and the appropriate module's POM
- [ ] JWT configuration properties (`secret`, `access-token-expiration-ms`, `refresh-token-expiration-ms`) are defined in `application.yaml`
- [ ] A `@ConfigurationProperties` class exists to bind the JWT properties
- [ ] `JwtService.java` exists with `generateAccessToken(User)` that creates a signed JWT with subject, roles claim, iat, and exp
- [ ] `JwtService.java` has `generateRefreshToken(User)` that creates a UUID string, persists it in `refresh_tokens` table, and returns it
- [ ] `JwtService.java` has `extractUsername(String token)` and `isTokenValid(String token)` methods
- [ ] Access token uses HMAC-SHA256 signing
- [ ] Access token TTL is configurable (default: 15 minutes)
- [ ] Refresh token TTL is configurable (default: 7 days)
- [ ] `mvn clean compile` passes from the project root
- [ ] The application starts without configuration errors
