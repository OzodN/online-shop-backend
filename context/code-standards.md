# Code Standards & Technical Decisions

---

## 3.4 Internal Module Structure (Layered)

Each module follows the same layered structure:

```
module-name/
├── src/main/java/dev/ozodn/onlineshop/modulename/
│   ├── controller/          # REST controllers
│   ├── service/             # Business logic
│   ├── repository/          # Spring Data JPA repositories
│   ├── entity/              # JPA entities
│   ├── dto/                 # Request/response records
│   ├── mapper/              # MapStruct mappers
│   ├── event/               # Domain events (published & consumed)
│   ├── exception/           # Module-specific exceptions
│   └── api/                 # Public interfaces exposed to other modules
├── src/main/resources/
│   └── db/migration/        # Flyway SQL migrations for this module's schema
└── pom.xml
```

---

## 12. Key Technical Decisions

### 12.1 Validation Strategy

- **Field-level:** Jakarta Bean Validation annotations (`@NotBlank`, `@Email`, `@Size`, `@Min`) on request DTOs
- **Business-level:** Custom validation in service layer (e.g., "stock must be >= requested quantity", "user must own this shop")

### 12.2 Pagination Response Format

```json
{
  "content": [...],
  "page": {
    "number": 0,
    "size": 20,
    "totalElements": 150,
    "totalPages": 8
  }
}
```

### 12.3 Logging

- SLF4J with Logback
- Log levels: `ERROR` for unexpected failures, `WARN` for business rule violations, `INFO` for significant events (user registered, order placed), `DEBUG` for detailed flow

### 12.4 Flyway Migration Naming

Each module has its own migration directory. Naming convention:

```
{module}/src/main/resources/db/migration/
  V1__create_{schema}_schema.sql
  V2__create_{table}_table.sql
  V3__add_{feature}.sql
```

---

## 13. Non-Functional Considerations

| Concern | Approach |
|---|---|
| **Concurrency** | Optimistic locking (`@Version`) on entities where concurrent updates are possible (product stock, order status) |
| **Security** | BCrypt password hashing, JWT token expiry, role-based endpoint security, input validation |
| **Performance** | GIN index on `search_vector`, proper DB indexes on FK columns and frequently queried fields |
| **Data Integrity** | DB constraints (NOT NULL, UNIQUE, CHECK), application-level validation, transactional boundaries per module |
