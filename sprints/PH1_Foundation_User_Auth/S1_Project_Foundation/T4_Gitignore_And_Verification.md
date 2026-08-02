# Task: Create .gitignore and Verify Full Project Startup
**Status:** [x] TODO / [ ] IN PROGRESS / [ ] DONE

## 🎯 Objective

Finalize Sprint 1 by creating a proper `.gitignore` file and performing a full end-to-end verification that everything works together.

You must:

**`.gitignore`:**
- Create a `.gitignore` in the project root tailored for a Java/Maven/IntelliJ project.
- Ignore: `target/`, `.idea/`, `*.iml`, `*.class`, `.DS_Store`, `*.log`, `application-local.yaml`, uploaded files directory.
- Do NOT ignore `docker-compose.yaml`, `context/`, `sprints/`, or `AGENTS.md`.

**Full verification:**
- Start PostgreSQL via `docker compose up -d`
- Verify the 4 schemas exist by connecting to the database
- Run `mvn clean compile` from the project root — must succeed
- Run `mvn spring-boot:run -pl app` — the application must start and connect to PostgreSQL
- Verify Spring Security is active (no 401 on requests)
- Verify Spring Modulith detects at least the `user` and `common` modules in startup logs
- Stop the app cleanly

This is the "definition of done" for Sprint 1 — if all of the above work, the project foundation is solid and we're ready for Sprint 2.

## 🧠 Context Files to Read
- `context/architecture/high-level-and-modules.md` — Expected project structure
- `context/project-overview.md` — Tech stack to verify everything is wired

## ✅ Acceptance Criteria
- [ ] `.gitignore` exists at the project root with appropriate Java/Maven/IDE patterns
- [ ] `docker compose up -d` starts PostgreSQL without errors
- [ ] The 4 schemas (`user_schema`, `product_schema`, `order_schema`, `review_schema`) exist in the database
- [ ] `mvn clean compile` succeeds from the project root
- [ ] `mvn spring-boot:run -pl app` starts the application without errors
- [ ] The app connects to PostgreSQL (datasource logs show successful connection)
- [ ] Spring Security is active but permissive (no 401 errors)
- [ ] Spring Modulith is detected in startup logs
- [ ] The project is ready to be committed to Git as the initial commit
