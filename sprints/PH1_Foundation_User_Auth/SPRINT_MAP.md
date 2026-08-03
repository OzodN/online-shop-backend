# 📋 Sprint Map — Phase 1: Foundation + User & Auth 🔐

> **Planning approach:** Rolling Wave Planning — S1 (complete) and S2 (current) have detailed task files. Future sprints are outlined but will be detailed when we reach them.

---

## Directory Structure

```
sprints/
└── PH1_Foundation_User_Auth/
    ├── SPRINT_MAP.md                          ← You are here
    ├── S1_Project_Foundation/                 ← ✅ COMPLETE
    │   ├── T1_Init_Maven_Multi_Module.md
    │   ├── T2_Docker_Compose_PostgreSQL.md
    │   ├── T3_Configure_Spring_Security_Modulith.md
    │   └── T4_Gitignore_And_Verification.md
    ├── S2_Common_Module/                      ← 🟢 CURRENT SPRINT
    │   ├── T1_BaseEntity_SoftDeletableEntity.md
    │   ├── T2_Global_Exception_Handler_RFC7807.md
    │   ├── T3_Shared_Pagination_DTOs.md
    │   └── T4_JPA_Auditing_Configuration.md
    ├── S3_User_Registration_And_Auth/         ← ⬜ PLANNED
    │   ├── T1_Flyway_Migrations_User_Schema.md
    │   ├── T2_User_Role_Entities_Repositories.md
    │   ├── T3_User_Registration_Endpoint.md
    │   ├── T4_JWT_Token_Service.md
    │   └── T5_Login_Refresh_Logout_SecurityConfig.md
    ├── S4_User_Profile_And_Addresses/         ← ⬜ PLANNED
    │   ├── T1_User_Profile_CRUD.md
    │   ├── T2_Become_Seller_Endpoint.md
    │   ├── T3_Address_Migration_Entity.md
    │   ├── T4_Address_CRUD_Endpoints.md
    │   └── T5_Swagger_OpenAPI_Setup.md
    └── S5_Testing_And_Verification/           ← ⬜ PLANNED
        ├── T1_Unit_Tests_AuthService.md
        ├── T2_Unit_Tests_UserService_AddressService.md
        ├── T3_Integration_Tests_Testcontainers.md
        └── T4_Spring_Modulith_Verification_Test.md
```

---

## Sprint Summary

| Sprint | Name | Focus | Tasks | Status |
|---|---|---|---|---|
| **S1** | Project Foundation | Maven multi-module, Docker Compose, Spring Security/Modulith config, .gitignore | 4 | ✅ Complete |
| **S2** | Common Module | BaseEntity, SoftDeletableEntity, RFC 7807 exception handler, shared DTOs, JPA auditing | 4 | 🟢 Current |
| **S3** | User Registration & Auth | Flyway migrations, User/Role entities, registration, JWT token service, login/refresh/logout | 5 | ⬜ Planned |
| **S4** | User Profile & Addresses | Profile CRUD, become-seller, address entity + CRUD, Swagger/OpenAPI | 5 | ⬜ Planned |
| **S5** | Testing & Verification | Unit tests, integration tests (Testcontainers), Spring Modulith architecture test | 4 | ⬜ Planned |

**Total Phase 1:** 5 sprints, ~22 tasks

---

## Notes

- Tasks for S2-S5 will be generated in detail when we reach each sprint.
- Each sprint builds on the previous one — do not skip ahead.
- After completing all S5 tasks, Phase 1 is complete and we move to Phase 2: Product & Catalog.
