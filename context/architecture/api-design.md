# Architecture — API Design

---

## 7. API Design

### 7.1 Conventions

| Aspect | Convention |
|---|---|
| **Base Path** | `/api/v1` |
| **Format** | JSON only |
| **Naming** | kebab-case for URLs, camelCase for JSON fields |
| **Auth Header** | `Authorization: Bearer <access-token>` |
| **Pagination** | `?page=0&size=20&sort=createdAt,desc` |
| **Error Format** | RFC 7807 Problem Details |
| **IDs in URLs** | Always UUID (never internal Long) |

### 7.2 Error Response Format (RFC 7807)

```json
{
  "type": "https://onlineshop.dev/errors/product-not-found",
  "title": "Product Not Found",
  "status": 404,
  "detail": "Product with ID 550e8400-e29b-41d4-a716-446655440000 was not found",
  "instance": "/api/v1/products/550e8400-e29b-41d4-a716-446655440000"
}
```

### 7.3 Endpoint Catalog

#### Auth & User Module

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Public | Register a new user (CUSTOMER role by default) |
| `POST` | `/api/v1/auth/login` | Public | Login, returns access + refresh tokens |
| `POST` | `/api/v1/auth/refresh` | Public | Refresh access token |
| `POST` | `/api/v1/auth/logout` | Authenticated | Revoke refresh token |
| `GET` | `/api/v1/users/me` | Authenticated | Get current user profile |
| `PUT` | `/api/v1/users/me` | Authenticated | Update current user profile |
| `POST` | `/api/v1/users/me/become-seller` | CUSTOMER | Elevate account to include SELLER role |
| `GET` | `/api/v1/users/me/addresses` | Authenticated | List user's addresses |
| `POST` | `/api/v1/users/me/addresses` | Authenticated | Add a new address |
| `PUT` | `/api/v1/users/me/addresses/{id}` | Authenticated | Update an address |
| `DELETE` | `/api/v1/users/me/addresses/{id}` | Authenticated | Delete an address |

#### Product & Catalog Module

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/shops` | SELLER | Create a shop |
| `GET` | `/api/v1/shops/{id}` | Public | Get shop details |
| `PUT` | `/api/v1/shops/{id}` | SELLER (owner) | Update shop |
| `GET` | `/api/v1/shops/{id}/products` | Public | List products in a shop |
| `POST` | `/api/v1/products` | SELLER | Create a product |
| `GET` | `/api/v1/products` | Public | List/search products (with filters & pagination) |
| `GET` | `/api/v1/products/{id}` | Public | Get product details |
| `PUT` | `/api/v1/products/{id}` | SELLER (owner) | Update product |
| `DELETE` | `/api/v1/products/{id}` | SELLER (owner) | Soft-delete product |
| `POST` | `/api/v1/products/{id}/images` | SELLER (owner) | Upload product image |
| `DELETE` | `/api/v1/products/{id}/images/{imageId}` | SELLER (owner) | Remove product image |
| `GET` | `/api/v1/categories` | Public | List all categories (tree) |
| `GET` | `/api/v1/categories/{id}/products` | Public | List products in category |
| `GET` | `/api/v1/products/search?q=...&category=...&minPrice=...&maxPrice=...` | Public | Full-text search with filters |

#### Cart & Order Module

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/v1/cart` | Authenticated | Get current user's cart |
| `POST` | `/api/v1/cart/items` | Authenticated | Add item to cart |
| `PUT` | `/api/v1/cart/items/{productId}` | Authenticated | Update item quantity |
| `DELETE` | `/api/v1/cart/items/{productId}` | Authenticated | Remove item from cart |
| `DELETE` | `/api/v1/cart` | Authenticated | Clear cart |
| `POST` | `/api/v1/orders` | Authenticated | Place order (checkout from cart) |
| `GET` | `/api/v1/orders` | Authenticated | List user's orders |
| `GET` | `/api/v1/orders/{id}` | Authenticated | Get order details (with sub-orders) |
| `POST` | `/api/v1/orders/{id}/cancel` | Authenticated | Cancel order |
| `GET` | `/api/v1/seller/orders` | SELLER | List sub-orders for seller's shop |
| `PUT` | `/api/v1/seller/orders/{subOrderId}/status` | SELLER | Update sub-order status (PROCESSING → SHIPPED → DELIVERED) |
| `POST` | `/api/v1/orders/{id}/pay` | Authenticated | Process mock payment |

#### Review & Wishlist Module

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `POST` | `/api/v1/products/{productId}/reviews` | Authenticated | Create review (must have purchased) |
| `GET` | `/api/v1/products/{productId}/reviews` | Public | List reviews for a product |
| `PUT` | `/api/v1/reviews/{id}` | Authenticated (author) | Update review |
| `DELETE` | `/api/v1/reviews/{id}` | Authenticated (author) | Delete review |
| `GET` | `/api/v1/wishlist` | Authenticated | Get user's wishlist |
| `POST` | `/api/v1/wishlist/{productId}` | Authenticated | Add product to wishlist |
| `DELETE` | `/api/v1/wishlist/{productId}` | Authenticated | Remove from wishlist |

#### File Serving

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| `GET` | `/api/v1/files/{filename}` | Public | Serve uploaded file/image |

---

## 12.5 Inter-Module API Interfaces

Each module exposes a public interface in its `api/` package. Other modules depend only on this interface:

```java
// In product module's api/ package
public interface ProductApi {
    ProductInfo getProductByExternalId(UUID externalId);
    boolean isProductAvailable(UUID externalId, int quantity);
    void reserveStock(UUID externalId, int quantity);
    void releaseStock(UUID externalId, int quantity);
}
```
