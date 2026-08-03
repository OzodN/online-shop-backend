# Task: Implement Global Exception Handler (RFC 7807)
**Status:** [x] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Create a centralized error handling system for the entire application using **RFC 7807 Problem Details** format. This ensures all modules return consistent, structured error responses.

You must implement:

**1. Base exception class** — a shared abstract/base `RuntimeException` that all module-specific exceptions will extend. Place it in `common/src/main/java/dev/ozodn/onlineshop/common/exception/`. Consider what common fields an exception should carry (e.g., HTTP status, error title, detail message).

**2. Common exception classes** — at minimum, create these reusable exceptions:
- `ResourceNotFoundException` — for when an entity is not found by UUID (HTTP 404)
- `DuplicateResourceException` — for unique constraint violations like duplicate email (HTTP 409)
- `AccessDeniedException` — for authorization failures beyond what Spring Security handles (HTTP 403)

**3. Global exception handler** — a `@RestControllerAdvice` class that catches exceptions and returns RFC 7807 responses. Place it in `common/src/main/java/dev/ozodn/onlineshop/common/exception/`.

The handler must handle at least:
- Your custom base exception → appropriate status code from the exception
- `MethodArgumentNotValidException` → 400 with validation error details
- `ConstraintViolationException` → 400 with validation error details
- `HttpMessageNotReadableException` → 400 (malformed JSON)
- Generic `Exception` → 500 (unexpected errors, log the stack trace)

**RFC 7807 response format:**
```json
{
  "type": "https://onlineshop.dev/errors/resource-not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Product with ID 550e8400-e29b-41d4-a716-446655440000 was not found",
  "instance": "/api/v1/products/550e8400-e29b-41d4-a716-446655440000"
}
```

> **Hint:** Spring Boot 3.x has built-in support for RFC 7807 via `ProblemDetail` class and `ErrorResponse` interface. Research whether you should use Spring's built-in `ProblemDetail` or create a custom DTO. Think about the trade-offs.

## 🧠 Context Files to Read
- `context/architecture/api-design.md` — Section 7.2 (RFC 7807 error response format with example)
- `context/ai-workflow-rules.md` — Rule 8 (error handling: single global `@RestControllerAdvice` in common module, modules define own exceptions extending shared base)
- `context/code-standards.md` — Section 12.3 (logging levels: ERROR for unexpected failures, WARN for business rule violations)

## ✅ Acceptance Criteria
- [ ] A base exception class exists in `common/.../exception/` that module-specific exceptions can extend
- [ ] `ResourceNotFoundException` exists and carries the resource type and identifier
- [ ] `DuplicateResourceException` exists for unique constraint violations
- [ ] A `@RestControllerAdvice` global exception handler exists in `common/.../exception/`
- [ ] The handler returns RFC 7807 `ProblemDetail` responses with `type`, `title`, `status`, `detail`, `instance` fields
- [ ] `MethodArgumentNotValidException` is handled (validation errors → 400)
- [ ] `ConstraintViolationException` is handled (validation errors → 400)
- [ ] Generic unhandled exceptions return 500 with a safe message (no stack trace in response, but logged)
- [ ] Error responses use correct logging levels (ERROR for 500s, WARN for 4xx)
- [ ] `mvn clean compile` passes from the project root
