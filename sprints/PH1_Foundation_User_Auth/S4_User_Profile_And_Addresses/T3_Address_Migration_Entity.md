# Task: Address — Flyway Migration & Entity
**Status:** [ ] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Create the `addresses` table in the `user_schema` database and its corresponding JPA entity. Addresses are used for shipping in the order module and belong to the `user` module. Each user can have multiple addresses with one marked as default.

You must implement:

**1. Flyway Migration** (`user/src/main/resources/db/migration/V3__create_addresses_table.sql`):
- Create the `addresses` table in `user_schema` with the following columns:

| Column | Type | Constraints |
|---|---|---|
| `id` | `BIGSERIAL` | PRIMARY KEY |
| `external_id` | `UUID` | NOT NULL, UNIQUE, DEFAULT `gen_random_uuid()` |
| `user_id` | `BIGINT` | NOT NULL, FK → `users(id)` ON DELETE CASCADE |
| `label` | `VARCHAR(50)` | NOT NULL (e.g., "Home", "Work") |
| `street` | `VARCHAR(255)` | NOT NULL |
| `city` | `VARCHAR(100)` | NOT NULL |
| `state` | `VARCHAR(100)` |  |
| `zip_code` | `VARCHAR(20)` | NOT NULL |
| `country` | `VARCHAR(100)` | NOT NULL |
| `is_default` | `BOOLEAN` | NOT NULL, DEFAULT `false` |
| `created_at` | `TIMESTAMP` | NOT NULL, DEFAULT `now()` |
| `updated_at` | `TIMESTAMP` | |
| `created_by` | `VARCHAR(255)` | |
| `updated_by` | `VARCHAR(255)` | |
| `deleted_at` | `TIMESTAMP` | |

- Add an index on `user_id` for efficient lookups: `CREATE INDEX IF NOT EXISTS idx_addresses_user_id ON user_schema.addresses(user_id);`
- Use `CREATE TABLE IF NOT EXISTS` for defensive SQL

**2. `Address.java` entity** (`user/entity/`):
- Extends `SoftDeletableEntity` (soft-delete per deletion strategy table — orders reference addresses)
- Maps to `user_schema.addresses`
- Fields: `label`, `street`, `city`, `state`, `zipCode`, `country`, `isDefault`
- `@ManyToOne(fetch = FetchType.LAZY)` relationship to `User` (via `user_id` FK)
- Use Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`
- `@Builder.Default` for `isDefault = false`

**3. `AddressRepository.java`** (`user/repository/`):
- `extends JpaRepository<Address, Long>`
- `List<Address> findByUserAndDeletedAtIsNull(User user)` — list active addresses for a user
- `Optional<Address> findByExternalIdAndDeletedAtIsNull(UUID externalId)` — find active address by UUID
- `Optional<Address> findByUserAndIsDefaultTrueAndDeletedAtIsNull(User user)` — find user's current default address

> **Think about:**
> - The `addresses` table uses soft delete (`deleted_at`) because orders reference shipping addresses — you can't hard-delete an address that's tied to an existing order.
> - `is_default` ensures only one address per user is the default. However, enforcing this at the DB level (with a partial unique index) is complex. For now, enforce it at the application level in the service (T4).
> - The migration version is `V3` because `V1` created user tables and `V2` added refresh token indexes.
> - The FK from `addresses.user_id → users.id` is within the same schema (`user_schema`), so it's allowed per Rule 2.

## 🧠 Context Files to Read
- `context/architecture/database-and-entities.md` — Section 6.2 (User Schema ERD — `addresses` table)
- `context/ai-workflow-rules.md` — Rule 2 (schema isolation), Rule 3 (Flyway naming), Rule 10 (soft delete), Rule 11 (auditing)
- `context/code-standards.md` — Section 12.4 (Flyway migration naming)
- Review existing: `V1__create_user_schema_tables.sql`, `V2__create_index_to_optimize_token_removal.sql`

## ✅ Acceptance Criteria
- [ ] `V3__create_addresses_table.sql` creates the `addresses` table in `user_schema` with all columns matching the ERD
- [ ] Migration includes an index on `user_id`
- [ ] Migration uses `CREATE TABLE IF NOT EXISTS` and `CREATE INDEX IF NOT EXISTS`
- [ ] `Address.java` entity extends `SoftDeletableEntity`
- [ ] `Address.java` has a `@ManyToOne(fetch = LAZY)` relationship to `User`
- [ ] `Address.java` uses Lombok annotations (`@Getter`, `@Setter`, `@Builder`, etc.)
- [ ] `AddressRepository.java` exists with methods to find addresses by user (filtered by `deletedAt IS NULL`)
- [ ] `mvn clean compile` passes from the project root
- [ ] The application starts successfully and Flyway runs `V3` migration without errors
