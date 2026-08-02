# Architecture — High-Level & Module Structure

---

## 3.1 High-Level Architecture

```mermaid
graph TB
    Client["REST API Clients<br/>(Swagger UI / Postman / Future Frontend)"]

    subgraph Application["Online Shop — Modular Monolith"]
        direction TB

        subgraph Shared["common module"]
            BaseEntity["Base Entity<br/>(audit fields, soft delete)"]
            SharedDTO["Shared DTOs<br/>(pagination, error responses)"]
            Exceptions["Exception Hierarchy"]
            Security["Security Utilities"]
        end

        subgraph Modules["Business Modules"]
            direction LR
            UserMod["user module"]
            ProductMod["product module"]
            OrderMod["order module"]
            ReviewMod["review module"]
        end
    end

    subgraph Infra["Infrastructure"]
        PG[("PostgreSQL")]
        FileStore[("Local File System")]
    end

    Client --> Application
    Modules --> Shared
    UserMod --> PG
    ProductMod --> PG
    OrderMod --> PG
    ReviewMod --> PG
    ProductMod --> FileStore
```

## 3.2 Module Overview

| Module | Responsibility | DB Schema | Key Entities |
|---|---|---|---|
| **common** | Cross-cutting concerns (base entity, shared DTOs, exceptions, security config) | — (no own schema) | `BaseEntity`, `ApiError` |
| **user** | Registration, authentication, profiles, roles, addresses | `user_schema` | `User`, `Role`, `Address` |
| **product** | Shops, products, categories, inventory, images, search | `product_schema` | `Shop`, `Product`, `Category`, `ProductImage` |
| **order** | Cart, checkout, orders, sub-orders, payment, order lifecycle | `order_schema` | `Cart`, `CartItem`, `Order`, `SubOrder`, `SubOrderItem`, `Payment` |
| **review** | Product reviews & ratings, wishlist | `review_schema` | `Review`, `WishlistItem` |

## 3.3 Module Communication

```mermaid
graph LR
    subgraph "Synchronous (Interface Calls)"
        OrderMod -- "getProductPrice()" --> ProductMod
        OrderMod -- "getUserById()" --> UserMod
        ReviewMod -- "getProductById()" --> ProductMod
        ReviewMod -- "hasUserPurchased()" --> OrderMod
    end

    subgraph "Asynchronous (Spring Events)"
        OrderMod -- "OrderPlacedEvent" --> ProductMod
        OrderMod -- "OrderCancelledEvent" --> ProductMod
        ProductMod -- "ProductDeletedEvent" --> OrderMod
        ProductMod -- "ProductDeletedEvent" --> ReviewMod
    end
```

**Rules:**
- **Queries (reads):** Direct method calls through exposed interfaces. Module A injects Module B's public interface.
- **Commands (state changes):** Domain events published via Spring's `ApplicationEventPublisher`. Subscribers react asynchronously.
- **No cross-module JPA joins:** Modules never reference each other's entities directly. Use IDs and interface calls.

---

## 4. Maven Project Structure

```
online-shop/                          # Parent POM
├── pom.xml                           # Parent POM (dependency management, plugins)
├── docker-compose.yaml               # PostgreSQL
├── common/                           # Shared module
│   ├── pom.xml
│   └── src/
├── user/                             # User & Auth module
│   ├── pom.xml
│   └── src/
├── product/                          # Product & Catalog module
│   ├── pom.xml
│   └── src/
├── order/                            # Cart & Order module
│   ├── pom.xml
│   └── src/
├── review/                           # Review & Wishlist module
│   ├── pom.xml
│   └── src/
└── app/                              # Application entry point (Spring Boot main class)
    ├── pom.xml                       # Depends on all modules
    └── src/
        └── main/
            ├── java/.../OnlineShopApplication.java
            └── resources/
                └── application.yaml
```

> [!IMPORTANT]
> The `app` module is the runnable Spring Boot application. It has dependencies on all business modules and contains the `main()` class, `application.yaml`, and global configuration.
