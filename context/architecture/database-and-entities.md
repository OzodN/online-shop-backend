# Architecture — Database & Entity Design

---

## 5. Entity Design

### 5.1 Base Entity

All entities extend a common `BaseEntity`:

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter @Setter
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private UUID externalId = UUID.randomUUID();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    @CreatedBy
    @Column(updatable = false)
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
```

For entities requiring soft delete, extend `SoftDeletableEntity`:

```java
@MappedSuperclass
@Getter @Setter
public abstract class SoftDeletableEntity extends BaseEntity {

    private Instant deletedAt;

    public boolean isDeleted() {
        return deletedAt != null;
    }

    public void softDelete() {
        this.deletedAt = Instant.now();
    }
}
```

### 5.2 ID Strategy

| Concern | Approach |
|---|---|
| **Internal PK** | `Long` auto-increment (`BIGSERIAL`) — used for joins, indexes, FK relationships |
| **External ID** | `UUID` — exposed in REST APIs, prevents enumeration |
| **API contract** | All endpoints accept/return UUID only. Internal Long is never exposed |

### 5.3 Deletion Strategy

| Entity | Strategy | Reason |
|---|---|---|
| `User` | Soft delete | Preserve order history references |
| `Product` | Soft delete | Orders reference products |
| `Shop` | Soft delete | Products reference shops |
| `Order`, `SubOrder` | Soft delete | Business records, never delete |
| `Review` | Soft delete | Audit trail |
| `CartItem` | Hard delete | Transient, no history needed |
| `WishlistItem` | Hard delete | Transient, no history needed |
| `Address` | Soft delete | Orders reference addresses |

---

## 6. Database Design

### 6.1 Schema Isolation

Each module owns a dedicated PostgreSQL schema:

```sql
CREATE SCHEMA IF NOT EXISTS user_schema;
CREATE SCHEMA IF NOT EXISTS product_schema;
CREATE SCHEMA IF NOT EXISTS order_schema;
CREATE SCHEMA IF NOT EXISTS review_schema;
```

> [!WARNING]
> Cross-schema foreign keys are **not allowed**. Modules reference each other via UUID only, enforced at the application level. This keeps modules decoupled and makes future microservice extraction possible.

### 6.2 Entity Relationship Diagrams

#### User Schema (`user_schema`)

```mermaid
erDiagram
    users {
        bigint id PK
        uuid external_id UK
        varchar email UK
        varchar password_hash
        varchar first_name
        varchar last_name
        varchar phone
        timestamp created_at
        timestamp updated_at
        varchar created_by
        varchar updated_by
        timestamp deleted_at
    }

    roles {
        bigint id PK
        varchar name UK "CUSTOMER, SELLER, ADMIN"
    }

    user_roles {
        bigint user_id FK
        bigint role_id FK
    }

    addresses {
        bigint id PK
        uuid external_id UK
        bigint user_id FK
        varchar label "Home, Work, etc."
        varchar street
        varchar city
        varchar state
        varchar zip_code
        varchar country
        boolean is_default
        timestamp created_at
        timestamp updated_at
        timestamp deleted_at
    }

    refresh_tokens {
        bigint id PK
        bigint user_id FK
        varchar token UK
        timestamp expires_at
        boolean revoked
        timestamp created_at
    }

    users ||--o{ user_roles : has
    roles ||--o{ user_roles : assigned_to
    users ||--o{ addresses : has
    users ||--o{ refresh_tokens : has
```

#### Product Schema (`product_schema`)

```mermaid
erDiagram
    shops {
        bigint id PK
        uuid external_id UK
        uuid owner_user_id "references user_schema.users.external_id"
        varchar name UK
        text description
        varchar logo_path
        timestamp created_at
        timestamp updated_at
        timestamp deleted_at
    }

    categories {
        bigint id PK
        uuid external_id UK
        varchar name
        varchar slug UK
        bigint parent_id FK "self-referencing"
        integer depth
        timestamp created_at
        timestamp updated_at
    }

    products {
        bigint id PK
        uuid external_id UK
        bigint shop_id FK
        bigint category_id FK
        varchar name
        varchar slug
        text description
        decimal price "USD, precision 19 scale 2"
        integer stock_quantity
        varchar status "ACTIVE, INACTIVE, OUT_OF_STOCK"
        tsvector search_vector "full-text search index"
        timestamp created_at
        timestamp updated_at
        varchar created_by
        varchar updated_by
        timestamp deleted_at
    }

    product_images {
        bigint id PK
        uuid external_id UK
        bigint product_id FK
        varchar file_path
        integer sort_order
        boolean is_primary
        timestamp created_at
    }

    shops ||--o{ products : sells
    categories ||--o{ products : categorizes
    categories ||--o{ categories : "parent of"
    products ||--o{ product_images : has
```

#### Order Schema (`order_schema`)

```mermaid
erDiagram
    carts {
        bigint id PK
        uuid external_id UK
        uuid user_id "references user_schema"
        timestamp created_at
        timestamp updated_at
    }

    cart_items {
        bigint id PK
        bigint cart_id FK
        uuid product_id "references product_schema"
        integer quantity
        decimal unit_price "snapshot at time of adding"
        timestamp created_at
        timestamp updated_at
    }

    orders {
        bigint id PK
        uuid external_id UK
        uuid user_id "references user_schema"
        uuid shipping_address_id "references user_schema"
        varchar status "PLACED, PAID, CANCELLED"
        decimal total_amount
        timestamp created_at
        timestamp updated_at
        timestamp deleted_at
    }

    sub_orders {
        bigint id PK
        uuid external_id UK
        bigint order_id FK
        uuid shop_id "references product_schema"
        varchar status "PLACED, PAID, PROCESSING, SHIPPED, DELIVERED, COMPLETED, CANCELLED, REFUNDED"
        decimal subtotal
        timestamp created_at
        timestamp updated_at
        timestamp deleted_at
    }

    sub_order_items {
        bigint id PK
        bigint sub_order_id FK
        uuid product_id "references product_schema"
        varchar product_name "snapshot"
        integer quantity
        decimal unit_price "snapshot"
        decimal total_price
    }

    payments {
        bigint id PK
        uuid external_id UK
        bigint order_id FK
        varchar payment_method "MOCK_CARD, MOCK_WALLET"
        varchar status "PENDING, SUCCESS, FAILED"
        decimal amount
        varchar transaction_id "mock ID"
        timestamp created_at
        timestamp updated_at
    }

    carts ||--o{ cart_items : contains
    orders ||--o{ sub_orders : "split into"
    sub_orders ||--o{ sub_order_items : contains
    orders ||--o| payments : "paid via"
```

#### Review Schema (`review_schema`)

```mermaid
erDiagram
    reviews {
        bigint id PK
        uuid external_id UK
        uuid user_id "references user_schema"
        uuid product_id "references product_schema"
        integer rating "1-5"
        text comment
        timestamp created_at
        timestamp updated_at
        timestamp deleted_at
    }

    wishlist_items {
        bigint id PK
        uuid user_id "references user_schema"
        uuid product_id "references product_schema"
        timestamp created_at
    }
```
