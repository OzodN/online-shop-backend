# Task: Become Seller Endpoint
**Status:** [ ] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Implement the self-service seller registration endpoint that allows any authenticated CUSTOMER to elevate their account to include the SELLER role. Per Architecture Decision #10, there is no admin approval required — any user can become a seller.

You must implement:

**1. Add to `UserService` / `UserServiceImpl`:**
- `UserResponse becomeSeller(UUID externalId)`:
  - Look up user by `externalId` → if not found, throw `ResourceNotFoundException`
  - Check if user already has the SELLER role → if yes, throw `DuplicateResourceException` (409 — "User already has the SELLER role") or return the existing profile idempotently (think about which behavior is more user-friendly)
  - Look up the SELLER role from `RoleRepository` → if not found, throw a runtime exception (this is a system error — the role should always exist from Flyway seed data)
  - Add the SELLER role to the user's roles set
  - Save the user
  - *(Optional: Publish a `UserBecameSellerEvent` via `ApplicationEventPublisher` — see domain events table in `flows-and-security.md`. No subscribers yet, but it sets up the eventing pattern for future use.)*
  - Return updated `UserResponse` (roles should now include both CUSTOMER and SELLER)

**2. Add to `UserController`:**
- `POST /api/v1/users/me/become-seller` — Authenticated (CUSTOMER only)
  - Extract `externalId` from security context
  - Call `userService.becomeSeller(externalId)`
  - Return 200 with updated `UserResponse`

**3. `RoleRepository.java`** (`user/repository/`):
- If it doesn't exist yet, create it: `extends JpaRepository<Role, Long>`
- Add: `Optional<Role> findByName(String name)` — needed to look up the SELLER role

> **Think about:**
> - Should any authenticated user be able to call this, or should it be restricted to users who are currently CUSTOMER-only? The API design says `CUSTOMER` auth. Use `@PreAuthorize("hasRole('CUSTOMER')")` or check in the service layer.
> - After becoming a seller, the user needs to re-login (or refresh their token) to get a new JWT with the updated roles claim. The current access token will still say `roles: ["CUSTOMER"]` until it expires or is refreshed. This is expected behavior for stateless JWTs — document this in the API response or Swagger description.
> - The `UserBecameSellerEvent` is defined in `flows-and-security.md` Section 10 (Domain Events) but has no subscribers yet. Publishing it now establishes the eventing pattern cleanly.
> - For `@PreAuthorize` to work, you need `@EnableMethodSecurity` somewhere in your configuration. Think about where.

## 🧠 Context Files to Read
- `context/architecture/api-design.md` — Section 7.3 (`POST /users/me/become-seller`)
- `context/architecture/flows-and-security.md` — Section 8.2 (RBAC roles), Section 10 (Domain Events — `UserBecameSellerEvent`)
- `context/ai-workflow-rules.md` — Rule 7 (layered architecture), Rule 5 (DTO discipline)
- Review existing: `user/entity/Role.java`, Flyway V1 migration (role seeding)

## ✅ Acceptance Criteria
- [ ] `RoleRepository.java` exists with `findByName(String)` method
- [ ] `UserService` has `becomeSeller(UUID externalId)` method
- [ ] `UserServiceImpl.becomeSeller()` adds the SELLER role to the user's roles set
- [ ] `UserServiceImpl.becomeSeller()` handles the case where the user already has the SELLER role (either 409 or idempotent — decide and document)
- [ ] `UserController` has `POST /api/v1/users/me/become-seller` endpoint
- [ ] The endpoint is restricted to authenticated users (ideally CUSTOMER-only via `@PreAuthorize` or service-level check)
- [ ] Updated `UserResponse` includes the new SELLER role
- [ ] `@EnableMethodSecurity` is configured (if using `@PreAuthorize`)
- [ ] `mvn clean compile` passes from the project root
- [ ] End-to-end: Register → Login → Become Seller → Refresh token → Verify new JWT contains SELLER role
