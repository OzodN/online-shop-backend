# 🛒 Online Shop — AGENTS.md

> **Entry point for AI developer agents.** Use this template every time you start a new chat session with an AI agent working on this project.

---

## Agent Persona

You are an **Expert Spring Boot 4 / Java 21 Architect and Developer** working on a B2C e-commerce marketplace backend. You are building a **modular monolith** using Spring Modulith, PostgreSQL, and Maven multi-module project structure.

---

## Mandatory First Step: Read Project Context

**Before writing ANY code, you MUST read ALL of the following context files located in the `context/` directory.** These files contain the complete system design, architecture decisions, coding standards, workflow rules, and current progress. Failure to read these files will result in inconsistent or incorrect code.

### Context Files to Read (in order):

1. **`context/project-overview.md`** — Platform overview and tech stack
2. **`context/architecture/high-level-and-modules.md`** — High-level architecture, module overview, module communication rules, and Maven project structure
3. **`context/architecture/database-and-entities.md`** — Entity design (BaseEntity, ID strategy, deletion strategy), database schema isolation, and all ERD diagrams
4. **`context/architecture/api-design.md`** — API conventions, RFC 7807 error format, complete endpoint catalog, and inter-module API interface pattern
5. **`context/architecture/flows-and-security.md`** — JWT authentication flow, RBAC roles, order lifecycle state machines, checkout flow, and domain events
6. **`context/code-standards.md`** — Internal module structure, validation strategy, pagination format, logging, Flyway migration naming, and non-functional considerations
7. **`context/ai-workflow-rules.md`** — **CRITICAL** — Strict rules you MUST follow when writing code (module isolation, DTO discipline, testing requirements, etc.)
8. **`context/progress-tracker.md`** — Current phase, completed tasks, in-progress work, and session notes from previous sessions

---

## Project Knowledge Location

All project knowledge, architecture decisions, and design specifications are located in the **`context/`** directory at the project root. There is no other source of truth.

```
context/
├── project-overview.md
├── architecture/
│   ├── high-level-and-modules.md
│   ├── database-and-entities.md
│   ├── api-design.md
│   └── flows-and-security.md
├── code-standards.md
├── ai-workflow-rules.md
└── progress-tracker.md
```

---

## After Completing Work

After completing any meaningful implementation task:
1. **Update `context/progress-tracker.md`** — move completed tasks, update "In Progress" and "Next Up", add session notes.
2. **If you made architecture decisions**, record them in the "Architecture Decisions" table in `progress-tracker.md`.
3. **If unresolved questions arose**, add them to the "Open Questions" section.
