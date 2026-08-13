# Task: Flyway Migrations for User Schema
**Status:** [x] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Create the Flyway SQL migrations that build the `user_schema` database tables needed for user registration and authentication. These migrations run automatically on application startup and must create the tables exactly as defined in the project's ERD.

You must create:

**Migration 1 — `V1__create_user_schema_tables.sql`:**

Create the following tables inside `user_schema`:

**`users` table:**
- `id` — `BIGSERIAL PRIMARY KEY`
- `external_id` — `UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()`
- `email` — `VARCHAR(255) NOT NULL UNIQUE`
- `password_hash` — `VARCHAR(255) NOT NULL`
- `first_name` — `VARCHAR(100) NOT NULL`
- `last_name` — `VARCHAR(100) NOT NULL`
- `phone` — `VARCHAR(20)` (nullable)
- `created_at` — `TIMESTAMP NOT NULL DEFAULT now()`
- `updated_at` — `TIMESTAMP`
- `created_by` — `VARCHAR(255)`
- `updated_by` — `VARCHAR(255)`
- `deleted_at` — `TIMESTAMP` (soft delete)

**`roles` table:**
- `id` — `BIGSERIAL PRIMARY KEY`
- `name` — `VARCHAR(50) NOT NULL UNIQUE`
- Pre-seed with three rows: `CUSTOMER`, `SELLER`, `ADMIN`

**`user_roles` join table:**
- `user_id` — `BIGINT NOT NULL` (FK → `users.id`, `ON DELETE CASCADE`)
- `role_id` — `BIGINT NOT NULL` (FK → `roles.id`, `ON DELETE CASCADE`)
- Composite primary key: `(user_id, role_id)`

**`refresh_tokens` table:**
- `id` — `BIGSERIAL PRIMARY KEY`
- `user_id` — `BIGINT NOT NULL` (FK → `users.id`, `ON DELETE CASCADE`)
- `token` — `VARCHAR(512) NOT NULL UNIQUE`
- `expires_at` — `TIMESTAMP NOT NULL`
- `revoked` — `BOOLEAN NOT NULL DEFAULT FALSE`
- `created_at` — `TIMESTAMP NOT NULL DEFAULT now()`

**Migration location:** `user/src/main/resources/db/migration/`

> **Think about:**
> - All tables MUST be prefixed with `SET search_path TO user_schema;` or use fully-qualified names like `user_schema.users`
> - The `roles` table is seeded with INSERT statements — what happens if the migration re-runs? (Hint: Flyway migrations are run once, so plain INSERTs are fine)
> - Why is `password_hash` called that and not `password`? (Hint: we never store plaintext passwords)
> - Why does `refresh_tokens` have `ON DELETE CASCADE`? (If a user is hard-deleted, their tokens should go too — but we soft-delete users, so this is a safety net)
> - The Flyway configuration currently has a global config in `application.yaml`. Each module needs its own migration directory — you'll need to configure Flyway to scan multiple locations. Check how `spring.flyway.locations` works.

## 🧠 Context Files to Read
- `context/architecture/database-and-entities.md` — Section 6.2, User Schema ERD (the exact table definitions)
- `context/ai-workflow-rules.md` — Rule 2 (database schema isolation), Rule 3 (Flyway migrations)
- `context/code-standards.md` — Section 12.4 (Flyway migration naming convention)

## ✅ Acceptance Criteria
- [ ] Migration file `V1__create_user_schema_tables.sql` exists at `user/src/main/resources/db/migration/`
- [ ] `users` table is created in `user_schema` with all columns matching the ERD
- [ ] `roles` table is created in `user_schema` with `CUSTOMER`, `SELLER`, `ADMIN` pre-seeded
- [ ] `user_roles` join table is created with composite PK and FK constraints
- [ ] `refresh_tokens` table is created with FK to `users` and `ON DELETE CASCADE`
- [ ] All tables use `user_schema` (via `SET search_path` or fully-qualified names)
- [ ] Flyway is configured to scan `user/src/main/resources/db/migration/` (update `application.yaml` if needed with `spring.flyway.locations`)
- [ ] `mvn clean compile` passes from the project root
- [ ] The Spring Boot application starts and Flyway runs the migration successfully (tables are created in PostgreSQL)
