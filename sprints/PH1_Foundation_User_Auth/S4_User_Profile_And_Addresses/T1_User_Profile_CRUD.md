# Task: User Profile CRUD (Get & Update)
**Status:** [ ] TODO / [ ] IN PROGRESS / [x] DONE

## 🎯 Objective

Implement the user profile endpoints that allow an authenticated user to view and update their own profile. This is a new `UserController` (separate from `AuthController`) with a `UserService`.

You must implement:

**1. `UserService` interface + `UserServiceImpl`** (`user/service/`):
- `UserResponse getProfile(UUID externalId)` — fetches the authenticated user's profile by their `externalId` (from the JWT `sub` claim)
  - Look up the user via `UserRepository.findByExternalId()` → if not found, throw `ResourceNotFoundException`
  - Map to `UserResponse` via `UserMapper`
- `UserResponse updateProfile(UUID externalId, UpdateProfileRequest request)` — updates the user's mutable profile fields
  - Look up user by `externalId` → if not found, throw `ResourceNotFoundException`
  - Update only the mutable fields: `firstName`, `lastName`, `phone`
  - **Do NOT allow updating:** `email`, `passwordHash`, `roles`, `externalId`, `deletedAt`
  - Save and return the updated `UserResponse`

**2. `UpdateProfileRequest.java`** (`user/dto/`) — Java Record:
- `firstName` (`@NotBlank`, `@Size(max = 100)`)
- `lastName` (`@NotBlank`, `@Size(max = 100)`)
- `phone` (`@Size(max = 20)`, nullable — user can clear their phone)

**3. `UserController.java`** (`user/controller/`):
- `GET /api/v1/users/me` — Authenticated — returns the current user's profile
  - Extract the user's `externalId` from the `SecurityContextHolder` (the JWT filter sets a `UsernamePasswordAuthenticationToken` — think about how to extract the UUID from the principal)
  - Call `userService.getProfile(externalId)`
  - Return 200 with `UserResponse`
- `PUT /api/v1/users/me` — Authenticated — updates the current user's profile
  - Extract `externalId` from security context
  - Call `userService.updateProfile(externalId, request)`
  - Return 200 with updated `UserResponse`

> **Think about:**
> - How do you extract the current user's UUID from `SecurityContextHolder`? The JWT filter sets a `UsernamePasswordAuthenticationToken` with a `UserDetails` principal. What does your `UserDetailsServiceImpl` set as the username? Think about how you can get the `externalId` from there.
> - Consider creating a utility method or helper class to extract the authenticated user's UUID from the security context — you'll need this in many places (address CRUD, become-seller, etc.).
> - Should `email` be updatable? For now, **no** — email changes require re-verification (out of scope). Keep it simple.
> - The `UserMapper` already has `toResponse(User)` from S3/T3. You can reuse it.
> - What happens if a user tries to `PUT /api/v1/users/me` but their account was soft-deleted between login and the request? The repository query `findByExternalId` already filters `deletedAt IS NULL`, so it will throw `ResourceNotFoundException`.

## 🧠 Context Files to Read
- `context/architecture/api-design.md` — Section 7.3 (User endpoints: `GET /users/me`, `PUT /users/me`)
- `context/ai-workflow-rules.md` — Rule 5 (DTO discipline), Rule 6 (UUID only), Rule 7 (layered architecture)
- `context/code-standards.md` — Section 12.1 (validation strategy)
- Review existing: `user/service/AuthService.java`, `user/mapper/UserMapper.java`, `user/dto/UserResponse.java`

## ✅ Acceptance Criteria
- [x] `UserService` interface exists with `getProfile(UUID)` and `updateProfile(UUID, UpdateProfileRequest)` methods
- [x] `UserServiceImpl` implements the interface with proper repository calls and MapStruct mapping
- [x] `UpdateProfileRequest.java` is a Java Record with `firstName`, `lastName`, `phone` + validation annotations
- [x] `UserController.java` exists with `GET /api/v1/users/me` and `PUT /api/v1/users/me` endpoints
- [x] Both endpoints extract the authenticated user's UUID from the `SecurityContextHolder`
- [x] `GET /users/me` returns 200 with `UserResponse`
- [x] `PUT /users/me` updates only `firstName`, `lastName`, `phone` — NOT `email`, `password`, `roles`
- [x] `PUT /users/me` returns 200 with updated `UserResponse`
- [x] `ResourceNotFoundException` (404) is thrown when user is not found or soft-deleted
- [x] `mvn clean compile` passes from the project root
- [x] Endpoints work end-to-end via Swagger UI (register → login → get profile → update profile)
