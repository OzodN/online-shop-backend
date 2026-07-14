# 🛒 Online Shop — Project Overview

> **B2C Marketplace Platform** — A modular monolith e-commerce backend built with Spring Boot 4, Java 21, and PostgreSQL.

---

## 1. Platform Overview

| Aspect | Decision |
|---|---|
| **Type** | B2C Marketplace (multiple sellers, many customers) |
| **Products** | Physical products only (single SKU, no variants) |
| **Currency** | USD only |
| **Shipping** | Free shipping (no cost calculation) |
| **Payments** | Mock/fake payment gateway |
| **Guest Checkout** | No — registration required |
| **Email** | Deferred — no email integration in initial scope |

---

## 2. Tech Stack

| Layer | Technology |
|---|---|
| **Language** | Java 21 |
| **Framework** | Spring Boot 4 |
| **Architecture** | Modular Monolith (Spring Modulith) |
| **Build Tool** | Maven (multi-module) |
| **Database** | PostgreSQL (separate schema per module) |
| **ORM** | Spring Data JPA (Hibernate) |
| **Migrations** | Flyway (SQL-based) |
| **Auth** | JWT (access + refresh tokens), Spring Security |
| **DTO Mapping** | MapStruct |
| **Boilerplate** | Lombok (entities) + Java Records (DTOs) |
| **API Docs** | springdoc-openapi (Swagger UI) |
| **Search** | PostgreSQL full-text search (`tsvector`/`tsquery`) |
| **File Storage** | Local disk |
| **Caching** | None initially (Spring `@Cacheable` with ConcurrentMap if needed) |
| **Containerization** | Docker Compose (PostgreSQL) |
| **Testing** | JUnit 5, Mockito, Testcontainers, Spring Modulith `@ApplicationModuleTest` |
