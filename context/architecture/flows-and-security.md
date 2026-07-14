# Architecture — Flows & Security

---

## 8. Authentication & Authorization

### 8.1 JWT Flow

```mermaid
sequenceDiagram
    participant C as Client
    participant A as Auth API
    participant DB as Database

    C->>A: POST /auth/register {email, password, name}
    A->>DB: Save user (CUSTOMER role)
    A-->>C: 201 Created

    C->>A: POST /auth/login {email, password}
    A->>DB: Verify credentials
    A-->>C: {accessToken (15min), refreshToken (7d)}

    C->>A: GET /api/v1/users/me (Bearer accessToken)
    A-->>C: 200 {user profile}

    Note over C,A: Access token expires...

    C->>A: POST /auth/refresh {refreshToken}
    A->>DB: Verify & rotate refresh token
    A-->>C: {new accessToken, new refreshToken}
```

### 8.2 Role-Based Access (RBAC)

| Role | Permissions |
|---|---|
| **CUSTOMER** | Browse products, manage cart, place orders, write reviews, manage wishlist, manage addresses |
| **SELLER** | Everything CUSTOMER can do + create shop, list products, manage inventory, fulfill orders |
| **ADMIN** | Everything + manage categories, view all users, manage platform |

---

## 9. Order Lifecycle (State Machine)

### 9.1 Order-Level States

```mermaid
stateDiagram-v2
    [*] --> PLACED : Checkout from cart
    PLACED --> PAID : Payment successful
    PLACED --> CANCELLED : Customer cancels / Payment fails
    PAID --> CANCELLED : Customer cancels (triggers refund)
    PAID --> COMPLETED : All sub-orders completed
    CANCELLED --> [*]
    COMPLETED --> [*]
```

### 9.2 Sub-Order-Level States (per seller)

```mermaid
stateDiagram-v2
    [*] --> PLACED
    PLACED --> PAID : Parent order paid
    PAID --> PROCESSING : Seller starts processing
    PROCESSING --> SHIPPED : Seller ships
    SHIPPED --> DELIVERED : Delivery confirmed
    DELIVERED --> COMPLETED : Auto or manual completion
    
    PLACED --> CANCELLED : Order cancelled
    PAID --> CANCELLED : Order cancelled
    PROCESSING --> CANCELLED : Seller cancels

    CANCELLED --> REFUNDED : Refund processed
    COMPLETED --> REFUNDED : Customer requests refund
    
    REFUNDED --> [*]
    COMPLETED --> [*]
    CANCELLED --> [*]
```

### 9.3 Checkout Flow

```mermaid
sequenceDiagram
    participant C as Customer
    participant Cart as Cart Service
    participant Order as Order Service
    participant Product as Product Service
    participant Payment as Payment Service

    C->>Order: POST /orders {addressId}
    Order->>Cart: Get cart items
    Cart-->>Order: Cart items with product UUIDs

    loop For each product in cart
        Order->>Product: Verify product exists, active, in stock
        Order->>Product: Reserve stock (decrement quantity)
    end

    Order->>Order: Group items by shop → create sub-orders
    Order->>Order: Create Order (status: PLACED)
    Order-->>C: 201 {orderId, total}

    C->>Payment: POST /orders/{id}/pay {paymentMethod}
    Payment->>Payment: Mock payment processing
    Payment-->>Order: Payment SUCCESS
    Order->>Order: Update status → PAID
    Order->>Order: Update all sub-orders → PAID
    Order-->>C: 200 {order with PAID status}

    Note over Cart: Cart is cleared after successful order placement
```

---

## 10. Domain Events

| Event | Publisher | Subscriber(s) | Purpose |
|---|---|---|---|
| `OrderPlacedEvent` | Order module | Product module | Reserve inventory (decrement stock) |
| `OrderCancelledEvent` | Order module | Product module | Release inventory (increment stock) |
| `OrderRefundedEvent` | Order module | Product module | Release inventory (increment stock) |
| `ProductDeletedEvent` | Product module | Order module, Review module | Handle deleted product references |
| `UserBecameSellerEvent` | User module | — (future use) | Track seller registrations |
