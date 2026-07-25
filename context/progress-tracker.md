# Progress Tracker

> This file must be updated after every meaningful implementation change.

---

## Current Status

- **Current Phase:** Phase 1 — Foundation + User & Auth 🔐 *(In Progress)*
- **Current Sprint:** S1 — Project Foundation
- **Current Task:** T1 — Initialize Maven Multi-Module Project
- **Current Goal:** Project skeleton, user registration, JWT auth, and address management.

---

## Phase 1: Foundation + User & Auth 🔐

**Goal:** Project skeleton, user registration, JWT auth, and address management.

**Sprints:** S1 → S2 → S3 → S4 → S5 (see `sprints/PH1_Foundation_User_Auth/SPRINT_MAP.md` for full breakdown)

| Sprint | Name | Status |
|---|---|---|
| S1 | Project Foundation | 🟢 In Progress |
| S2 | Common Module | ⬜ Not Started |
| S3 | User Registration & Auth | ⬜ Not Started |
| S4 | User Profile & Addresses | ⬜ Not Started |
| S5 | Testing & Verification | ⬜ Not Started |

### Completed
*(none yet)*

### In Progress
- **[S1]** Initialize Maven multi-module project structure (parent POM + `common`, `user`, `app` modules)
- **[S1]** Set up Docker Compose with PostgreSQL
- **[S1]** Configure Spring Boot 3.5, Spring Security, Spring Modulith

### Next Up
- **[S2]** Implement `common` module:
   - `BaseEntity` and `SoftDeletableEntity`
   - Global exception handler (RFC 7807)
   - Shared pagination DTOs
   - JPA auditing configuration
- **[S3]** Implement `user` module:
   - User registration (default CUSTOMER role)
   - Login (JWT access + refresh tokens)
   - Token refresh and logout
   - Flyway migrations for `user_schema`
- **[S4]** Implement `user` module (continued):
   - User profile CRUD
   - "Become seller" endpoint
   - Address CRUD
   - Set up Swagger/OpenAPI
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

---

## Session Notes

*(Context needed to resume work in the next session)*

**Session 1 (2026-07-13):** Completed system design brainstorming and created comprehensive system design document. Decomposed the design into modular knowledge base context files. No code has been written yet.

**Session 2 (2026-07-15):** Assembled the virtual AI team (5 agents: Architecture Guardian, Code Quality Reviewer, Security Analyst, DBA Reviewer, QA Testing Coach). Created sprint map for Phase 1 (5 sprints, ~22 tasks). Generated detailed task files for Sprint 1 (4 tasks). Ready to begin coding T1: Initialize Maven Multi-Module Project.

**Session 3 (2026-07-25):** Completed T1: Initialize Maven Multi-Module Project. Code review by Architecture Guardian and Code Quality Reviewer identified: (1) main class was in wrong package (`dev.ozodn.onlineshop.app` → moved to `dev.ozodn.onlineshop`), (2) groupId inconsistency across modules (unified to `dev.ozodn.onlineshop` for all modules), (3) missing dependency management entries (added Lombok, Testcontainers BOM, PostgreSQL driver, Flyway), (4) decided on Spring Boot 3.5.x over 4.x due to ecosystem compatibility, (5) Lombok 1.18.36→1.18.38 to fix `ExceptionInInitializerError: TypeTag :: UNKNOWN` incompatibility with JDK 21. All issues resolved. `mvn clean compile` passes. Ready for T2: Docker Compose + PostgreSQL.
