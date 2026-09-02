# Task: Address CRUD Endpoints
**Status:** [ ] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Implement the full address CRUD endpoints so that authenticated users can manage their shipping addresses. Each user can have multiple addresses with one marked as default.

You must implement:

**1. DTOs** (`user/dto/`):
- `CreateAddressRequest.java` — Java Record:
  - `label` (`@NotBlank`, `@Size(max = 50)`) — e.g., "Home", "Work"
  - `street` (`@NotBlank`, `@Size(max = 255)`)
  - `city` (`@NotBlank`, `@Size(max = 100)`)
  - `state` (`@Size(max = 100)`, nullable)
  - `zipCode` (`@NotBlank`, `@Size(max = 20)`)
  - `country` (`@NotBlank`, `@Size(max = 100)`)
  - `isDefault` (boolean, default `false`)
- `UpdateAddressRequest.java` — Java Record (same fields as create)
- `AddressResponse.java` — Java Record:
  - `externalId` (UUID), `label`, `street`, `city`, `state`, `zipCode`, `country`, `isDefault`, `createdAt` (Instant)

**2. `AddressMapper.java`** (`user/mapper/`):
- MapStruct mapper (`@Mapper(componentModel = "spring")`)
- `Address toEntity(CreateAddressRequest request)` — maps DTO → entity (does NOT set user — that's done in the service)
- `AddressResponse toResponse(Address address)`
- `void updateEntity(@MappingTarget Address address, UpdateAddressRequest request)` — updates existing entity fields from DTO

**3. `AddressService` interface + `AddressServiceImpl`** (`user/service/`):
- `List<AddressResponse> getAddresses(UUID userExternalId)`:
  - Look up user → find all active addresses for that user → map to responses
- `AddressResponse createAddress(UUID userExternalId, CreateAddressRequest request)`:
  - Look up user → map request to entity → set user on entity
  - If `isDefault = true`, unset the current default address first (set `isDefault = false` on the old default)
  - Save and return response
- `AddressResponse updateAddress(UUID userExternalId, UUID addressExternalId, UpdateAddressRequest request)`:
  - Look up address by `addressExternalId` → verify it belongs to the authenticated user (compare `address.getUser()` with the looked-up user) → if not, throw `ResourceNotFoundException` (don't reveal the address exists to other users)
  - If `isDefault` is being set to `true`, unset the current default first
  - Update entity fields → save → return response
- `void deleteAddress(UUID userExternalId, UUID addressExternalId)`:
  - Look up address → verify ownership → soft-delete (`address.softDelete()`)
  - If the deleted address was the default, **do not** auto-promote another address — let the user explicitly set a new default
  - Return nothing (204)

**4. `AddressController.java`** (`user/controller/`):
- `GET /api/v1/users/me/addresses` — Authenticated — list addresses
- `POST /api/v1/users/me/addresses` — Authenticated — create address (201 Created)
- `PUT /api/v1/users/me/addresses/{id}` — Authenticated — update address (200 OK)
  - `{id}` is the address UUID (`externalId`)
- `DELETE /api/v1/users/me/addresses/{id}` — Authenticated — soft-delete address (204 No Content)

> **Think about:**
> - **Ownership verification** is critical: a user should only be able to view/update/delete their own addresses. Never expose another user's address, even as a 403 — use 404 to avoid revealing that the address exists.
> - **Default address logic**: When setting an address as default, unset the previous default first. Use `AddressRepository.findByUserAndIsDefaultTrueAndDeletedAtIsNull()` to find the current default.
> - **Soft delete**: Use `address.softDelete()` (sets `deletedAt = Instant.now()`) from `SoftDeletableEntity`. All repository queries must filter `deletedAt IS NULL`.
> - Reuse the security context utility you created in T1 to extract the user's UUID.
> - The `{id}` in the URL path is a UUID (`@PathVariable UUID id`), not a Long — per Rule 6.

## 🧠 Context Files to Read
- `context/architecture/api-design.md` — Section 7.3 (Address endpoints)
- `context/architecture/database-and-entities.md` — Section 6.2 (User Schema ERD — `addresses` table)
- `context/ai-workflow-rules.md` — Rule 5 (DTO discipline), Rule 6 (UUID only), Rule 10 (soft delete)
- Review existing: `user/mapper/UserMapper.java` (for MapStruct patterns)

## ✅ Acceptance Criteria
- [ ] `CreateAddressRequest`, `UpdateAddressRequest`, `AddressResponse` are Java Records with proper validation
- [ ] `AddressMapper` (MapStruct) maps between DTOs and `Address` entity
- [ ] `AddressService` interface exists with `getAddresses`, `createAddress`, `updateAddress`, `deleteAddress`
- [ ] `AddressServiceImpl` implements all methods with ownership verification
- [ ] Setting `isDefault = true` unsets the previous default address for that user
- [ ] `deleteAddress` performs a soft delete (sets `deletedAt`)
- [ ] Ownership is verified on update and delete (returns 404 if address doesn't belong to user)
- [ ] `AddressController` has all 4 endpoints under `/api/v1/users/me/addresses`
- [ ] `POST` returns 201, `DELETE` returns 204
- [ ] `{id}` path variable is UUID, not Long
- [ ] `mvn clean compile` passes from the project root
- [ ] End-to-end: Create address → List addresses → Update address → Set new default → Delete address → Verify soft-deleted
