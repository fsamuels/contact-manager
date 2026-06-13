# Roadmap

## Short-term

### Add Spring Security
The app has no authentication or CSRF protection. Minimum for a local app:
- Add `spring-boot-starter-security`
- Configure a simple in-memory user store (or form-based login)
- Enable CSRF protection for the REST API (or use stateless token auth)
- Protect all `/api/**` and SPA routes

### SPA UX polish
- Handle 404 gracefully when a deep-linked UUID no longer exists (auto-redirect to list)
- Add a 404 fallback route in the SPA router

### Frontend test setup
The Vue code has no tests. Add Vitest + Vue Test Utils and cover:
- `personValidation.ts` (pure functions — easy wins)
- `parseJsonResponse` / `ApiError` mapping
- View-level happy paths with a mocked fetch layer

## Medium-term

### Production database
H2 in-memory is development-only. For a persistent deployment:
- Add a PostgreSQL (or MySQL) dependency and connection properties
- Introduce a migration tool (Flyway or Liquibase) to manage schema changes
  (currently `ddl-auto=none` with manual SQL scripts)
- Add a `docker-compose.yml` for local dev with a real database

### CI/CD
- Add a GitHub Actions workflow: `mvn verify` on push/PR
- Add frontend linting (`eslint`) and type-checking (`vue-tsc --noEmit`) to CI
- Build and publish the JAR artifact

## Long-term

### Search and filtering
- Add a search/filter bar to the person list (name or email substring)
- Server-side: extend `PersonRepository` with a `findByNameContaining` derived query
- SPA: query-param-driven (`?q=`) so the URL is shareable

### Observability
- Spring Boot Actuator (health, metrics)
- Structured request logging

## Technical debt

| Item | Impact | Blocked on |
|---|---|---|
| No frontend tests | No unit or component tests for Vue code | — |
| Font Awesome via CDN | External dependency; icons fail offline | Low priority |
| Unused JSP-era CSS selectors | `styles.css` was moved wholesale; some selectors may no longer match any markup | Audit after UI settles |

## Nice-to-have

- Sort the person list by clicking column headers
- Inline edit (edit in-place in the list row)
- Bulk delete (multi-select checkboxes)
- Note character count / remaining indicator
- Dark-mode system preference detection (`prefers-color-scheme` → default theme)
