# Progress Tracker

> This file must be updated after every meaningful implementation change.

---

## Current Status

- **Current Phase:** Phase 1 — Foundation + User & Auth 🔐 *(In Progress)*
- **Current Sprint:** S4 — User Profile & Addresses
- **Current Task:** T2 — Become Seller Endpoint
- **Current Goal:** Project skeleton, user registration, JWT auth, and address management.

---

## Phase 1: Foundation + User & Auth 🔐

**Goal:** Project skeleton, user registration, JWT auth, and address management.

**Sprints:** S1 → S2 → S3 → S4 → S5 (see `sprints/PH1_Foundation_User_Auth/SPRINT_MAP.md` for full breakdown)

| Sprint | Name | Status |
|---|---|---|
| S1 | Project Foundation | ✅ Complete |
| S2 | Common Module | ✅ Complete |
| S3 | User Registration & Auth | ✅ Complete |
| S4 | User Profile & Addresses | 🟢 In Progress |
| S5 | Testing & Verification | ⬜ Not Started |

### Completed
- **[S1/T1]** Initialize Maven multi-module project structure (parent POM + `common`, `user`, `app` modules) ✅
- **[S1/T2]** Set up Docker Compose with PostgreSQL ✅
- **[S1/T3]** Configure Spring Boot 3.5, Spring Security, Spring Modulith ✅
- **[S1/T4]** Gitignore and full project verification ✅
- **[S2/T1]** Implement `BaseEntity` and `SoftDeletableEntity` in `common` module ✅
- **[S2/T2]** Implement Global Exception Handler (RFC 7807) in `common` module ✅
- **[S2/T3]** Implement Shared Pagination DTOs in `common` module ✅
- **[S2/T4]** Configure JPA Auditing in `common` module ✅
- **[S3/T1]** Flyway Migrations for User Schema ✅
- **[S3/T2]** User, Role, RefreshToken Entities & Repositories ✅
- **[S3/T3]** User Registration Endpoint ✅
- **[S3/T4]** JWT Token Service ✅
- **[S3/T5]** Login, Refresh, Logout, & Security Filter ✅
- **[S4/T1]** User Profile CRUD ✅

### In Progress
*(none)*

### Next Up
- **[S4]** Implement `user` module (continued):
   - "Become seller" endpoint
   - Address migration & entity
   - Address CRUD
   - OpenAPI global config (OpenApiConfig class + Swagger UI verification)
- **[S5]** Testing:
   - Write unit tests + integration tests with Testcontainers
   - Add Spring Modulith architecture verification test

**Deliverable:** A running app where users can register, login, manage their profile and addresses.

---

## Phase 2: Product & Catalog 📦 *(Not Started)*

**Goal:** Sellers can create shops and list products. Customers can browse.

**Sprints:** To be planned when Phase 1 completes.

### Completed
*(none yet)*

### In Progress
*(none yet)*

### Next Up
1. Create `product` module
2. Implement category management (hierarchical tree with self-referencing)
3. Implement shop CRUD (create, update, view)
4. Implement product CRUD (create, update, soft-delete, list by shop/category)
5. Implement product image upload (local file storage)
6. Implement file serving endpoint
7. Implement inventory tracking (stock quantity on products)
8. Implement PostgreSQL full-text search with filters (category, price range, etc.)
9. Flyway migrations for `product_schema`
10. Authorization: only shop owners can manage their products
11. Unit + integration tests

**Deliverable:** Sellers can create a shop and list products. Customers can search/browse the catalog.

---

## Phase 3: Cart & Order 🛒 *(Not Started)*

**Goal:** End-to-end purchase flow from cart to order completion.

**Sprints:** To be planned when Phase 2 completes.

### Completed
*(none yet)*

### In Progress
*(none yet)*

### Next Up
1. Create `order` module
2. Implement cart (add items, update quantity, remove, clear)
3. Cart validation against product service (stock check, product active)
4. Implement checkout flow (cart → order with sub-orders per seller)
5. Implement order state machine (PLACED → PAID → ... → COMPLETED/CANCELLED)
6. Implement sub-order state machine (seller fulfillment flow)
7. Implement mock payment service
8. Implement inventory reservation/release via domain events
9. Seller order management endpoints (view sub-orders, update status)
10. Customer order history
11. Flyway migrations for `order_schema`
12. Unit + integration tests

**Deliverable:** Complete purchase lifecycle — browse → add to cart → checkout → pay → seller fulfills → delivered.

---

## Phase 4: Reviews, Wishlist & Polish ⭐ *(Not Started)*

**Goal:** Social features and final polish.

**Sprints:** To be planned when Phase 3 completes.

### Completed
*(none yet)*

### In Progress
*(none yet)*

### Next Up
1. Create `review` module
2. Implement product reviews (only for purchased products)
3. Calculate and expose average rating on products
4. Implement wishlist (add/remove/list)
5. Flyway migrations for `review_schema`
6. Cross-module integration (verify purchase before review)
7. Final Spring Modulith architecture tests
8. API documentation polish
9. End-to-end integration tests
10. Unit + integration tests

**Deliverable:** Full-featured marketplace backend with reviews and wishlists.

---

## Open Questions

*(Any unresolved product or technical decisions)*

*(none yet)*

---

## Architecture Decisions

*(Decisions made that affect the system design or data model — include why the decision was made)*

| # | Decision | Rationale | Date |
|---|---|---|---|
| 1 | Modular monolith with Spring Modulith | Simpler than microservices for a pet project; enforces module boundaries at test time; easy to extract to microservices later if needed | 2026-07-13 |
| 2 | Separate PostgreSQL schemas per module | Provides real DB-level isolation between modules; prevents accidental cross-module table access; makes future microservice extraction easier | 2026-07-13 |
| 3 | Multi-module Maven project | Gives strongest compile-time isolation — Module A literally cannot import Module B's internals because they are in different JARs | 2026-07-13 |
| 4 | Long (internal PK) + UUID (external ID) | Long for DB performance (joins, indexes); UUID for API exposure (prevents enumeration, no leaked table size) | 2026-07-13 |
| 5 | Mix of direct calls (queries) and events (commands) for inter-module communication | Direct calls are simpler for reads; events decouple modules for state changes and side effects | 2026-07-13 |
| 6 | JWT stateless auth (access + refresh tokens) | Standard for modern APIs; no server-side session storage needed; pairs well with future frontend/mobile clients | 2026-07-13 |
| 7 | Soft delete for core entities, hard delete for transient data | Preserves business records and audit trails for orders/users/products; transient items like cart items don't need history | 2026-07-13 |
| 8 | PostgreSQL full-text search instead of Elasticsearch | No extra infrastructure; powerful enough for an e-commerce catalog; uses tsvector/tsquery with GIN indexes | 2026-07-13 |
| 9 | No product variants (single SKU per product) | Keeps the product domain simpler for a pet project; can be added later if needed | 2026-07-13 |
| 10 | Self-service seller registration (no admin approval) | Keeps the scope manageable for a pet project; any user can become a seller | 2026-07-13 |
| 11 | Rolling Wave Planning with sprint-based task management | Only the current sprint has detailed tasks; future sprints are outlined but detailed when reached | 2026-07-15 |
| 12 | Spring Boot 3.5.x instead of 4.x | Some Spring ecosystem dependencies (Spring Modulith, springdoc-openapi, etc.) not yet fully compatible with Spring Boot 4; revisit when ecosystem catches up | 2026-07-25 |
| 13 | Use `.yaml` extension instead of `.yml` for all YAML files | `.yaml` is the official extension per the YAML specification; consistency across `docker-compose.yaml`, `application.yaml`, and all future config files | 2026-08-02 |
| 14 | Swagger Writer agent writes OpenAPI annotations (exception to mentorship model) | Swagger annotations are repetitive documentation boilerplate that don't build engineering skills; developer focuses on business logic while agent handles `@Operation`, `@Tag`, `@ApiResponse`, `@Schema` after code review passes | 2026-08-24 |
| 15 | Javadoc Writer agent adds Javadoc comments (exception to mentorship model) | Javadoc is mechanical documentation boilerplate; uses targeted file-list approach (~3,100 tokens/invocation) with `flash` model; covers services, entities, repositories, exceptions; skips DTOs, mappers, configs; Swagger Writer keeps controller method docs | 2026-08-28 |

---

## Session Notes

> Session notes have been moved to [`context/session-notes.md`](session-notes.md) to keep this file focused on status tracking.

