# Progress Tracker

> This file must be updated after every meaningful implementation change.

---

## Current Status

- **Current Phase:** Phase 1 — Foundation + User & Auth 🔐 *(Not Started)*
- **Current Goal:** Project skeleton, user registration, JWT auth, and address management.

---

## Phase 1: Foundation + User & Auth 🔐

**Goal:** Project skeleton, user registration, JWT auth, and address management.

### Completed
*(none yet)*

### In Progress
*(none yet)*

### Next Up
1. Initialize Maven multi-module project structure (parent POM + `common`, `user`, `app` modules)
2. Set up Docker Compose with PostgreSQL
3. Configure Spring Boot 4, Spring Security, Spring Modulith
4. Implement `common` module:
   - `BaseEntity` and `SoftDeletableEntity`
   - Global exception handler (RFC 7807)
   - Shared pagination DTOs
   - JPA auditing configuration
5. Implement `user` module:
   - User registration (default CUSTOMER role)
   - Login (JWT access + refresh tokens)
   - Token refresh and logout
   - User profile CRUD
   - "Become seller" endpoint
   - Address CRUD
   - Flyway migrations for `user_schema`
6. Set up Swagger/OpenAPI
7. Write unit tests + integration tests with Testcontainers
8. Add Spring Modulith architecture verification test

**Deliverable:** A running app where users can register, login, manage their profile and addresses.

---

## Phase 2: Product & Catalog 📦 *(Not Started)*

**Goal:** Sellers can create shops and list products. Customers can browse.

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

---

## Session Notes

*(Context needed to resume work in the next session)*

**Last session (2026-07-13):** Completed system design brainstorming and created comprehensive system design document. Decomposed the design into modular knowledge base context files. No code has been written yet. Ready to begin Phase 1.
