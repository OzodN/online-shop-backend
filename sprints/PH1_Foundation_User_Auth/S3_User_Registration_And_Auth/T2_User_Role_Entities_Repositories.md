# Task: User, Role, and RefreshToken Entities & Repositories
**Status:** [x] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Create the JPA entity classes and Spring Data JPA repositories for the `user` module. These entities map to the tables created by the Flyway migration in T1 and form the persistence layer for user registration and authentication.

You must implement:

**1. `User` entity** (`user/src/main/java/dev/ozodn/onlineshop/user/entity/User.java`):
- Extends `SoftDeletableEntity` (from `common` module — users are soft-deleted)
- Maps to `user_schema.users` table (`@Table(name = "users", schema = "user_schema")`)
- Fields: `email`, `passwordHash`, `firstName`, `lastName`, `phone`
- `@ManyToMany` relationship with `Role` via `user_roles` join table (fetch = `LAZY`, cascade — think carefully about which cascades make sense here)
- Uses Lombok: `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`
- Column mappings must use `@Column(name = "...")` where the Java field name differs from the DB column name (e.g., `passwordHash` → `password_hash`)

**2. `Role` entity** (`user/src/main/java/dev/ozodn/onlineshop/user/entity/Role.java`):
- Extends `BaseEntity` (roles are never deleted)
- Maps to `user_schema.roles` table
- Fields: `name` (String, unique)
- Uses Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@AllArgsConstructor`

**3. `RefreshToken` entity** (`user/src/main/java/dev/ozodn/onlineshop/user/entity/RefreshToken.java`):
- Extends `BaseEntity`
- Maps to `user_schema.refresh_tokens` table
- Fields: `user` (`@ManyToOne` to `User`, fetch = `LAZY`), `token`, `expiresAt`, `revoked`
- Uses Lombok: `@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`

**4. `UserRepository`** (`user/src/main/java/dev/ozodn/onlineshop/user/repository/UserRepository.java`):
- `JpaRepository<User, Long>`
- Methods: `Optional<User> findByEmail(String email)`, `Optional<User> findByExternalId(UUID externalId)`, `boolean existsByEmail(String email)`

**5. `RoleRepository`** (`user/src/main/java/dev/ozodn/onlineshop/user/repository/RoleRepository.java`):
- `JpaRepository<Role, Long>`
- Methods: `Optional<Role> findByName(String name)`

**6. `RefreshTokenRepository`** (`user/src/main/java/dev/ozodn/onlineshop/user/repository/RefreshTokenRepository.java`):
- `JpaRepository<RefreshToken, Long>`
- Methods: `Optional<RefreshToken> findByToken(String token)`, `void deleteByUser(User user)`

> **Think about:**
> - `User` extends `SoftDeletableEntity` (which extends `BaseEntity`). That means `id`, `externalId`, `createdAt`, `updatedAt`, `createdBy`, `updatedBy`, and `deletedAt` are ALL inherited. Don't re-declare them!
> - `Role` extends `BaseEntity` — it inherits audit fields. But `roles` table in the ERD only has `id` and `name`. Should `Role` really extend `BaseEntity`? Think about whether roles need audit fields and external IDs. If not, maybe it's a simple `@Entity` with just `@Id` and `name`. This is a design decision for you to make.
> - For the `@ManyToMany` on `User`, who is the owning side? (Hint: the side that defines `@JoinTable` is the owner)
> - `RefreshToken` has `createdAt` but does it need `createdBy`/`updatedBy`? It inherits them from `BaseEntity` — they'll be populated by JPA auditing. Is that okay?
> - For `findByEmail` queries on `User` — should these filter out soft-deleted users? Think about whether a soft-deleted user should be able to log in.
> - Delete the placeholder `Main.java` in the user module if it still exists.

## 🧠 Context Files to Read
- `context/architecture/database-and-entities.md` — Section 5.1 (BaseEntity), 5.3 (deletion strategy), 6.2 (User Schema ERD)
- `context/ai-workflow-rules.md` — Rule 5 (DTO discipline — entities use Lombok), Rule 7 (layered architecture), Rule 10 (soft delete), Rule 12 (base package)
- `context/code-standards.md` — Section 3.4 (module structure — `entity/` and `repository/` packages)

## ✅ Acceptance Criteria
- [ ] `User.java` exists in `user/entity/` — extends `SoftDeletableEntity`, maps to `user_schema.users`
- [ ] `User` has `email`, `passwordHash`, `firstName`, `lastName`, `phone` fields with correct `@Column` mappings
- [ ] `User` has `@ManyToMany` relationship with `Role` via `user_roles` join table
- [ ] `Role.java` exists in `user/entity/` — maps to `user_schema.roles` with `name` field
- [ ] `RefreshToken.java` exists in `user/entity/` — maps to `user_schema.refresh_tokens` with `user`, `token`, `expiresAt`, `revoked`
- [ ] `UserRepository.java` exists in `user/repository/` with `findByEmail`, `findByExternalId`, `existsByEmail`
- [ ] `RoleRepository.java` exists in `user/repository/` with `findByName`
- [ ] `RefreshTokenRepository.java` exists in `user/repository/` with `findByToken`, `deleteByUser`
- [ ] All entities use appropriate Lombok annotations (`@Getter`, `@Setter`, `@Builder`, etc.)
- [ ] The placeholder `Main.java` in the user module is deleted (if it exists)
- [ ] `mvn clean compile` passes from the project root
- [ ] The Spring Boot application starts without entity mapping errors (Hibernate validates against the Flyway-created schema)
