# Task: OpenAPI Global Configuration
**Status:** [ ] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Create the global OpenAPI configuration class that powers the "Authorize" button in Swagger UI and defines the API metadata. Without this configuration, authenticated endpoints cannot be tested through the Swagger UI because there is no way to input the Bearer JWT token.

You must implement:

**1. `OpenApiConfig.java`** (`app/src/main/java/dev/ozodn/onlineshop/config/`):
- Annotate with `@Configuration`
- Add `@OpenAPIDefinition` with API metadata:
  - `info.title` = "Online Shop API"
  - `info.version` = "1.0.0"
  - `info.description` = A brief description of the Online Shop marketplace API
- Add `@SecurityScheme` annotation:
  - `name = "bearerAuth"` — **MUST match** the name used in `@SecurityRequirement(name = "bearerAuth")` on authenticated controller endpoints
  - `type = SecuritySchemeType.HTTP`
  - `scheme = "bearer"`
  - `bearerFormat = "JWT"`

> **Think about:**
> - This class goes in the `app` module because Swagger serves the entire application (all modules). The `app` module is the entry point that assembles everything.
> - The `name = "bearerAuth"` string MUST exactly match what the Swagger Writer agent used in `@SecurityRequirement(name = "bearerAuth")` on `AuthController.logout()`, and what will be used on all future authenticated endpoints.
> - After creating this class, the Swagger UI at `/swagger-ui/index.html` should show a green "Authorize" button at the top. Clicking it reveals a text field where you can paste a JWT token (without the `Bearer ` prefix — Swagger adds it automatically).
> - Verify that the `SecurityConfig` already allows access to Swagger UI paths (`/swagger-ui/**`, `/v3/api-docs/**`). Check the existing configuration.
> - This is a simple configuration class — no business logic, no service calls.

## 🧠 Context Files to Read
- `context/ai-workflow-rules.md` — Rule 13 (OpenAPI documentation requirements)
- Review existing: `app/src/main/java/dev/ozodn/onlineshop/config/SecurityConfig.java` (verify Swagger paths are permitted)
- Review existing: `user/controller/AuthController.java` (see `@SecurityRequirement(name = "bearerAuth")` usage)

## ✅ Acceptance Criteria
- [ ] `OpenApiConfig.java` exists in `app/src/main/java/dev/ozodn/onlineshop/config/`
- [ ] `@OpenAPIDefinition` sets API title, version, and description
- [ ] `@SecurityScheme` defines `bearerAuth` with type HTTP, scheme bearer, format JWT
- [ ] The `name` in `@SecurityScheme` matches the `name` in `@SecurityRequirement` on authenticated endpoints
- [ ] Swagger UI shows the green "Authorize" button at `/swagger-ui/index.html`
- [ ] After authorizing with a valid JWT, authenticated endpoints (like `POST /logout`) return successful responses instead of 401
- [ ] `mvn clean compile` passes from the project root
- [ ] The application starts without configuration errors
