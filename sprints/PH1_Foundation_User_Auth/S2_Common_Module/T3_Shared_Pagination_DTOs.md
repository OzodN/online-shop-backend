# Task: Implement Shared Pagination DTOs
**Status:** [x] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Create shared pagination response DTOs in the `common` module that all modules will use when returning paginated lists. This ensures every list endpoint in the application returns a consistent response format.

You must implement:

**`PagedResponse<T>`** — a generic wrapper for paginated data. This is a **Java Record** (immutable DTO) that wraps Spring Data's `Page<T>` into our standardized format.

The response format must match the project standard:
```json
{
  "content": [ ... ],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8
  }
}
```

You'll need:
1. **`PagedResponse<T>`** — the outer record with `content` (list of items) and `page` (page metadata)
2. **`PageMetadata`** — a nested record (or separate record) with `number`, `size`, `totalElements`, `totalPages`
3. A **static factory method** on `PagedResponse` that accepts a `Page<T>` from Spring Data and converts it to your format (e.g., `PagedResponse.from(Page<T> page)`)

**Package location:** `common/src/main/java/dev/ozodn/onlineshop/common/dto/`

> **Think about:**
> - DTOs must be Java Records (Rule 5 from ai-workflow-rules.md)
> - The generic `<T>` lets this work for any entity type across all modules
> - Controllers will call `PagedResponse.from(productPage)` instead of returning Spring's raw `Page<T>` — this decouples our API contract from Spring Data internals

## 🧠 Context Files to Read
- `context/code-standards.md` — Section 12.2 (pagination response format — exact JSON structure to follow)
- `context/ai-workflow-rules.md` — Rule 5 (DTO discipline: all DTOs must be Java Records)
- `context/code-standards.md` — Section 3.4 (internal module structure showing `dto/` package)

## ✅ Acceptance Criteria
- [ ] `PagedResponse.java` exists at `common/.../dto/PagedResponse.java` as a Java Record
- [ ] `PagedResponse<T>` has a `content` field (List<T>) and a `page` field (page metadata)
- [ ] Page metadata contains: `number` (int), `size` (int), `totalElements` (long), `totalPages` (int)
- [ ] A static factory method `PagedResponse.from(Page<T>)` exists to convert Spring Data's Page
- [ ] The JSON output matches the project standard format (`content` + `page` with nested fields)
- [ ] Both DTOs are Java Records (not classes)
- [ ] `mvn clean compile` passes from the project root
