# AI Workflow Rules

> Strict rules that any AI agent writing code for this project MUST follow.

---

## Rule 1: Module Isolation

- **No direct JPA joins between modules.** Modules MUST NOT reference each other's JPA entities directly.
- Cross-module data access is done exclusively via **UUID references** and **public `api/` package interfaces**.
- For read operations (queries): inject and call the target module's public interface from the `api/` package.
- For write operations (state changes/notifications): publish **Spring domain events** via `ApplicationEventPublisher`. The subscriber module reacts to the event.
- Never import classes from another module's `entity/`, `repository/`, `service/`, or `controller/` packages. Only the `api/` and `event/` packages are public contracts.

## Rule 2: Database Schema Isolation

- Each module owns its own PostgreSQL schema (`user_schema`, `product_schema`, `order_schema`, `review_schema`).
- **Cross-schema foreign keys are forbidden.** Referential integrity between modules is enforced at the application level, not the database level.
- Modules reference each other's data via `UUID` columns (the `external_id` of the target entity), never via `Long` internal primary keys.

## Rule 3: Flyway Migrations

- Each module MUST have its own independent Flyway migrations directory at `{module}/src/main/resources/db/migration/`.
- Migration files follow the naming convention: `V{N}__description.sql` (e.g., `V1__create_user_schema.sql`).
- Migrations MUST only create/alter tables within the module's own schema.
- Never write a migration that touches another module's schema.

## Rule 4: Spring Modulith Architecture Tests

- Any change to module structure, package layout, or inter-module dependencies MUST pass Spring Modulith verification tests.
- The project includes `@ApplicationModuleTest` tests that verify module boundaries are respected.
- Before submitting any structural change, ensure the modulith architecture verification test passes.

## Rule 5: DTO Discipline

- **Never expose JPA entities from REST controllers.** Controllers MUST only accept and return DTOs.
- All request/response DTOs MUST be implemented as **Java Records** (immutable).
- Entity ↔ DTO mapping MUST use **MapStruct** mapper interfaces. No manual mapping in controllers or services.
- Entities use **Lombok** annotations (`@Getter`, `@Setter`, `@Builder`, `@NoArgsConstructor`, `@AllArgsConstructor`) for boilerplate reduction.

## Rule 6: ID Exposure

- REST API endpoints MUST only accept and return **UUID** (`externalId`) for entity identification.
- The internal `Long id` (auto-increment primary key) MUST NEVER be exposed in API requests or responses.
- When looking up entities by UUID, use repository methods like `findByExternalId(UUID externalId)`.

## Rule 7: Layered Architecture Per Module

- Every module MUST follow the layered structure: `controller/` → `service/` → `repository/` → `entity/`.
- Additionally, each module has: `dto/`, `mapper/`, `event/`, `exception/`, and `api/` packages.
- Controllers call services. Services call repositories. This flow is strictly top-down — no reverse dependencies.
- Business logic lives in the `service/` layer, never in controllers or repositories.

## Rule 8: Error Handling

- All API errors MUST use the **RFC 7807 Problem Details** format.
- There is a single global `@RestControllerAdvice` exception handler in the `common` module.
- Modules define their own exception classes (extending a shared base) in their `exception/` package.

## Rule 9: Testing Requirements

- Every service class MUST have **unit tests** (JUnit 5 + Mockito).
- Every module MUST have **integration tests** using **Testcontainers** (PostgreSQL) and `@SpringBootTest` or `@ApplicationModuleTest`.
- Test classes follow the naming convention: `{ClassName}Test.java` for unit tests, `{ClassName}IntegrationTest.java` for integration tests.

## Rule 10: Soft Delete Compliance

- Entities marked for soft delete (`User`, `Product`, `Shop`, `Order`, `SubOrder`, `Review`, `Address`) MUST extend `SoftDeletableEntity` and use the `deletedAt` timestamp field.
- All repository queries for soft-deletable entities MUST filter out deleted records (where `deletedAt IS NULL`) unless explicitly querying for deleted records.
- Entities marked for hard delete (`CartItem`, `WishlistItem`) are physically removed from the database.

## Rule 11: Auditing

- All entities extend `BaseEntity` which provides `createdAt`, `updatedAt`, `createdBy`, and `updatedBy` fields via JPA auditing (`@EntityListeners(AuditingEntityListener.class)`).
- JPA auditing MUST be enabled in the application configuration.

## Rule 12: Base Package

- The base Java package is `dev.ozodn.onlineshop`.
- Each module's code lives under `dev.ozodn.onlineshop.{modulename}` (e.g., `dev.ozodn.onlineshop.user`, `dev.ozodn.onlineshop.product`).

## Rule 13: OpenAPI Documentation

- Every `@RestController` class MUST have a `@Tag(name, description)` annotation.
- Every endpoint method MUST have `@Operation(summary, description)` and `@ApiResponse` annotations for all expected status codes.
- Authenticated endpoints MUST declare `@SecurityRequirement(name = "bearerAuth")`.
- Request/Response DTO records SHOULD have `@Schema(description, example)` on non-obvious fields.
- OpenAPI annotations are added by the **Swagger Writer agent** after code review passes, not by the developer manually.
- The Swagger Writer agent's system prompt is stored at `context/agent-prompts/swagger-writer.md`.
