# Task: Set Up Docker Compose with PostgreSQL
**Status:** [ ] TODO / [ ] IN PROGRESS / [x] DONE

## 🎯 Objective

Create a `docker-compose.yaml` file in the project root that spins up a PostgreSQL 16 container for local development. The database must be pre-configured with the 4 schemas our modules will use.

You must set up:
- **`docker-compose.yaml`** — defines a `postgres` service with:
  - PostgreSQL 16 image
  - Database name: `onlineshop`
  - Username/password for development (e.g., `onlineshop` / `onlineshop`)
  - Port mapping: `5432:5432`
  - A named volume for data persistence
  - A health check so dependent services know when PG is ready
- **`init.sql`** (mounted into the container) — a SQL initialization script that creates the 4 schemas:
  - `user_schema`
  - `product_schema`
  - `order_schema`
  - `review_schema`
- **`application.yaml`** in the `app` module — configure:
  - Spring datasource pointing to the Docker PostgreSQL (`jdbc:postgresql://localhost:5432/onlineshop`)
  - JPA/Hibernate properties (ddl-auto: `validate`, show-sql: false, dialect)
  - Flyway enabled with schema configuration
  - Server port (e.g., 8080)

## 🧠 Context Files to Read
- `context/architecture/database-and-entities.md` — Schema isolation section (6.1) with exact schema names
- `context/architecture/high-level-and-modules.md` — Maven project structure showing where docker-compose.yaml lives
- `context/project-overview.md` — Tech stack confirming PostgreSQL and Docker Compose

## ✅ Acceptance Criteria
- [x] `docker-compose.yaml` exists in the project root
- [x] Running `docker compose up -d` starts PostgreSQL 16 successfully
- [x] The 4 schemas (`user_schema`, `product_schema`, `order_schema`, `review_schema`) are created on startup via `init.sql`
- [x] `application.yaml` exists at `app/src/main/resources/application.yaml` with correct datasource config
- [x] JPA is configured with `ddl-auto: validate` (Flyway manages schema, not Hibernate)
- [x] Flyway is enabled in the configuration
- [x] The Spring Boot app can start and connect to the database (may fail on Flyway if no migrations exist yet — that's expected and OK at this stage)