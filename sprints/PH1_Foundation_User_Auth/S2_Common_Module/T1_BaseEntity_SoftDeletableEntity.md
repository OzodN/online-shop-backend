# Task: Implement BaseEntity and SoftDeletableEntity
**Status:** [x] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Create the two foundational abstract entity classes that **every JPA entity in the project will extend**. These live in the `common` module under the `entity/` package and provide audit fields, the dual-ID strategy, and soft-delete support.

You must implement:

**`BaseEntity`** — the root mapped superclass:
- `Long id` — auto-increment primary key (`@GeneratedValue(strategy = GenerationType.IDENTITY)`), internal only, **never exposed in APIs**
- `UUID externalId` — public-facing identifier, initialized to `UUID.randomUUID()`, `unique`, `not null`, `updatable = false`
- `Instant createdAt` — auto-populated by JPA auditing (`@CreatedDate`), `not null`, `updatable = false`
- `Instant updatedAt` — auto-populated by JPA auditing (`@LastModifiedDate`)
- `String createdBy` — auto-populated by JPA auditing (`@CreatedBy`), `updatable = false`
- `String updatedBy` — auto-populated by JPA auditing (`@LastModifiedBy`)
- Must use `@MappedSuperclass`, `@EntityListeners(AuditingEntityListener.class)`, and Lombok `@Getter @Setter`

**`SoftDeletableEntity`** — extends `BaseEntity`, adds soft-delete:
- `Instant deletedAt` — null means active, non-null means soft-deleted
- `boolean isDeleted()` method — returns `deletedAt != null`
- `void softDelete()` method — sets `deletedAt = Instant.now()`
- Must use `@MappedSuperclass` and Lombok `@Getter @Setter`

**Package location:** `common/src/main/java/dev/ozodn/onlineshop/common/entity/`

> **Think about:** You can delete the placeholder `Main.java` file in the common module — it's no longer needed.

## 🧠 Context Files to Read
- `context/architecture/database-and-entities.md` — Section 5.1 (BaseEntity) and 5.2 (ID strategy), 5.3 (deletion strategy). Contains the exact reference code.
- `context/ai-workflow-rules.md` — Rule 6 (ID exposure), Rule 10 (soft delete compliance), Rule 11 (auditing), Rule 12 (base package)
- `context/code-standards.md` — Section 3.4 (internal module structure showing `entity/` package)

## ✅ Acceptance Criteria
- [ ] `BaseEntity.java` exists at `common/src/main/java/dev/ozodn/onlineshop/common/entity/BaseEntity.java`
- [ ] `BaseEntity` is `@MappedSuperclass` with `@EntityListeners(AuditingEntityListener.class)`
- [ ] `BaseEntity` has all 6 fields: `id` (Long), `externalId` (UUID), `createdAt`, `updatedAt`, `createdBy`, `updatedBy`
- [ ] `externalId` is initialized to `UUID.randomUUID()` and annotated `unique = true, updatable = false, nullable = false`
- [ ] `id` uses `@GeneratedValue(strategy = GenerationType.IDENTITY)`
- [ ] Audit fields use `@CreatedDate`, `@LastModifiedDate`, `@CreatedBy`, `@LastModifiedBy`
- [ ] `BaseEntity` uses Lombok `@Getter @Setter`
- [ ] `SoftDeletableEntity.java` exists at `common/src/main/java/dev/ozodn/onlineshop/common/entity/SoftDeletableEntity.java`
- [ ] `SoftDeletableEntity` extends `BaseEntity` and adds `deletedAt`, `isDeleted()`, `softDelete()`
- [ ] The placeholder `Main.java` is deleted
- [ ] `mvn clean compile` passes from the project root
