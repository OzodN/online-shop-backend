# Task: Initialize Maven Multi-Module Project
**Status:** [x] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Create the foundational Maven multi-module project structure for the Online Shop modular monolith. This is the skeleton of the entire project — a parent POM with three initial child modules: `common`, `user`, and `app`.

You must set up:
- **Parent POM** (`online-shop/pom.xml`) — defines `<modules>`, manages dependency versions via `<dependencyManagement>`, and configures shared plugins. Must include Spring Boot 4 as the parent, Java 21 compiler settings, and declare all libraries we'll use (Spring Data JPA, Spring Security, Spring Modulith, Flyway, MapStruct, Lombok, PostgreSQL driver, springdoc-openapi, Testcontainers, etc.).
- **`common` module** (`common/pom.xml`) — a JAR module with no Spring Boot plugin. Contains shared code (base entities, exceptions, DTOs). Depends on Spring Data JPA, Lombok, Jakarta Validation.
- **`user` module** (`user/pom.xml`) — a JAR module. Depends on `common`. Will contain user/auth logic. Depends on Spring Web, Spring Security, Spring Data JPA, Flyway, MapStruct, Lombok.
- **`app` module** (`app/pom.xml`) — the runnable Spring Boot application. Depends on `common`, `user`, and later all other modules. Contains the `@SpringBootApplication` main class and `application.yml`. Has the `spring-boot-maven-plugin`.

Each module must use the base package `dev.ozodn.onlineshop`.

## 🧠 Context Files to Read
- `context/architecture/high-level-and-modules.md` — Maven project structure tree and module overview
- `context/project-overview.md` — Full tech stack with all libraries to include
- `context/code-standards.md` — Internal module layered structure (package layout)

## ✅ Acceptance Criteria
- [ ] Parent POM exists at `online-shop/pom.xml` with `<modules>` listing `common`, `user`, `app`
- [ ] Parent POM uses Spring Boot 4 as `<parent>` with Java 21 `<maven.compiler.source/target>`
- [ ] `<dependencyManagement>` declares versions for: Spring Modulith BOM, MapStruct, Lombok, springdoc-openapi, Testcontainers BOM, PostgreSQL driver, Flyway
- [ ] `common/pom.xml` exists as a JAR module with `<parent>` pointing to the parent POM
- [ ] `user/pom.xml` exists as a JAR module with dependency on `common`
- [ ] `app/pom.xml` exists with dependencies on `common` and `user`, has `spring-boot-maven-plugin`
- [ ] `app/src/main/java/dev/ozodn/onlineshop/OnlineShopApplication.java` exists with `@SpringBootApplication`
- [ ] `mvn clean compile` runs successfully from the project root with no errors
