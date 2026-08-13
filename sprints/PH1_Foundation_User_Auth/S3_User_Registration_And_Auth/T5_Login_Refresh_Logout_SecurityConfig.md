# Task: Login, Refresh, Logout Endpoints & Security Filter Configuration
**Status:** [x] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Complete the authentication flow by implementing login, token refresh, and logout endpoints, plus the JWT authentication filter that secures all protected endpoints. This task also updates the `SecurityConfig` to wire in the JWT filter and define which endpoints are public vs. authenticated.

You must implement:

**1. Login endpoint — `POST /api/v1/auth/login`** (Public):
- Request DTO (`LoginRequest.java`): `email` (`@NotBlank`, `@Email`), `password` (`@NotBlank`)
- Response DTO (`AuthResponse.java`): `accessToken` (String), `refreshToken` (String), `expiresIn` (Long — seconds until access token expires)
- In `AuthService.login()`:
  - Look up user by email → if not found, throw authentication error
  - Verify password using `PasswordEncoder.matches()` → if wrong, throw authentication error
  - Check if user is soft-deleted (`deletedAt != null`) → if deleted, throw authentication error
  - Generate access token via `JwtService.generateAccessToken()`
  - Generate refresh token via `JwtService.generateRefreshToken()`
  - Return `AuthResponse`
- **Security note:** Use the same generic error message for "user not found" AND "wrong password" — never reveal whether an email exists

**2. Token refresh endpoint — `POST /api/v1/auth/refresh`** (Public):
- Request DTO (`RefreshTokenRequest.java`): `refreshToken` (`@NotBlank`)
- Response: `AuthResponse` (new access + refresh tokens)
- In `AuthService.refresh()`:
  - Look up refresh token in DB → if not found, throw authentication error
  - Check if token is revoked → if revoked, throw authentication error
  - Check if token is expired (`expiresAt < now`) → if expired, throw authentication error
  - Revoke the old refresh token (set `revoked = true`)
  - Generate new access token and new refresh token (token rotation)
  - Return `AuthResponse`
- **Token rotation:** The old refresh token is invalidated and a new one is issued. This prevents token reuse attacks.

**3. Logout endpoint — `POST /api/v1/auth/logout`** (Authenticated):
- Request DTO (`RefreshTokenRequest.java` — reuse from above): `refreshToken`
- In `AuthService.logout()`:
  - Look up refresh token in DB → if not found, silently succeed (idempotent logout)
  - Revoke the refresh token (set `revoked = true`)
  - Return 204 No Content

**4. JWT Authentication Filter** (`JwtAuthenticationFilter.java`):
- Extends `OncePerRequestFilter`
- For each incoming request:
  - Extract the `Authorization` header
  - If absent or doesn't start with `Bearer `, continue the filter chain (no auth)
  - Extract the JWT from the header (strip `Bearer ` prefix)
  - Extract the username (subject) from the JWT via `JwtService`
  - If the username is not null and no authentication exists in `SecurityContextHolder`:
    - Load the user from the database (by email/subject)
    - Validate the JWT via `JwtService.isTokenValid()`
    - If valid, create a `UsernamePasswordAuthenticationToken` with the user's details and authorities (roles), and set it in the `SecurityContextHolder`
  - Continue the filter chain
- Place it in `app/config/` or `user/config/` — think about where it belongs (Hint: the filter needs `JwtService` from `user` and must be registered in `SecurityConfig` in `app`)
- **Security note:** The filter must handle exceptions gracefully — a malformed JWT should NOT crash the request; it should simply not authenticate

**5. `UserDetailsService` implementation:**
- Implement Spring Security's `UserDetailsService` interface (or use a custom approach)
- The filter needs to load a user by username (email) to create the auth token
- Think about where this lives — `user/service/`? It's part of the user module's responsibility.

**6. Update `SecurityConfig`** (in `app/config/SecurityConfig.java`):
- Register the `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`
- Define endpoint authorization rules:
  - `POST /api/v1/auth/register` — `permitAll()`
  - `POST /api/v1/auth/login` — `permitAll()`
  - `POST /api/v1/auth/refresh` — `permitAll()`
  - All other `/api/v1/**` — `authenticated()`
- Keep CSRF disabled (stateless API)
- Keep session management stateless
- Add `AuthenticationManager` bean (needed for `AuthService`)

> **Think about:**
> - The JWT filter runs on EVERY request. It must be fast and must never throw exceptions to the client — either authenticate or pass through.
> - Why does logout only revoke the refresh token and not "invalidate" the access token? (Hint: JWTs are stateless — once issued, they can't be revoked. The access token will expire in 15 minutes naturally. If you need immediate revocation, you'd need a token blacklist — out of scope for now.)
> - Token rotation on refresh: when the client refreshes, the old refresh token is revoked and a new one is issued. If an attacker steals a refresh token and tries to use it after the legitimate user has already refreshed, it will be revoked → detected!
> - `AnonymousAuthenticationToken` — remember your `AuditorAware` from S2/T4 handles this case. The JWT filter and auditing work together.
> - For error responses on failed authentication, you should return RFC 7807 Problem Details. Think about adding an `@ExceptionHandler` for `AuthenticationException` in your `GlobalExceptionHandler` or a custom `AuthenticationEntryPoint`.

## 🧠 Context Files to Read
- `context/architecture/flows-and-security.md` — Section 8.1 (JWT Flow: login → access+refresh, refresh → rotate, logout → revoke)
- `context/architecture/api-design.md` — Section 7.3 (Auth endpoints: login, refresh, logout + auth requirements)
- `context/ai-workflow-rules.md` — Rule 5 (DTO discipline), Rule 7 (layered architecture), Rule 8 (RFC 7807 errors)
- `context/code-standards.md` — Section 12.1 (validation), Section 12.3 (logging)
- Review `app/src/main/java/dev/ozodn/onlineshop/config/SecurityConfig.java` (the existing config you'll modify)

## ✅ Acceptance Criteria
- [ ] `LoginRequest.java` is a Java Record with `email` and `password` fields + validation
- [ ] `AuthResponse.java` is a Java Record with `accessToken`, `refreshToken`, and `expiresIn`
- [ ] `RefreshTokenRequest.java` is a Java Record with `refreshToken` field
- [ ] `AuthService.login()` authenticates user and returns access + refresh tokens
- [ ] `AuthService.login()` returns the same error for "user not found" and "wrong password" (no information leakage)
- [ ] `AuthService.login()` rejects soft-deleted users
- [ ] `AuthService.refresh()` validates refresh token, revokes the old one, and issues new tokens (rotation)
- [ ] `AuthService.logout()` revokes the refresh token (idempotent)
- [ ] `AuthController` has `POST /login`, `POST /refresh`, and `POST /logout` endpoints
- [ ] `POST /logout` returns HTTP 204 No Content
- [ ] `JwtAuthenticationFilter` extracts Bearer tokens from `Authorization` header
- [ ] `JwtAuthenticationFilter` sets `SecurityContextHolder` authentication on valid JWT
- [ ] `JwtAuthenticationFilter` handles malformed/expired JWTs gracefully (no exceptions thrown to client)
- [ ] `SecurityConfig` registers `JwtAuthenticationFilter` before `UsernamePasswordAuthenticationFilter`
- [ ] `SecurityConfig` permits auth endpoints and requires authentication for all other `/api/v1/**`
- [ ] `UserDetailsService` (or equivalent) loads users by email for the filter
- [ ] `mvn clean compile` passes from the project root
- [ ] End-to-end flow works (test manually):
  - Register → Login (get tokens) → Access protected endpoint with Bearer token → Refresh → Logout → Verify old refresh token is rejected
