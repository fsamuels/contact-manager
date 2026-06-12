# Roadmap

## Short-term

### Remove the JSP layer
The SPA reached feature parity (person CRUD, notes, themes), so the JSP
layer is surplus. Removal steps:
1. Delete `src/main/webapp/` and all JSP controllers/views
2. Remove `tomcat-embed-jasper`, JSTL, and JSP-related properties
3. Remove `spring.mvc.view.*` properties
4. Switch `<packaging>war</packaging>` to `jar` in `pom.xml`
5. Convert `Page<T>` from a class to a record (JSP EL constraint removed)
6. Remove the JSP-specific `@InitBinder` and `StringTrimmerEditor` from web controllers
7. Repoint the application root `/` from `/persons` to `/app/`
8. Remove the `ClassicUiLink` component from the SPA

### SPA UX polish
- Handle 404 gracefully when a deep-linked UUID no longer exists (auto-redirect to list)
- Add a 404 fallback route in the SPA router

## Medium-term

### Add Spring Security
The app has no authentication or CSRF protection. Minimum for a local app:
- Add `spring-boot-starter-security`
- Configure a simple in-memory user store (or form-based login)
- Enable CSRF protection for the REST API (or use stateless token auth)
- Protect all `/api/**` and SPA routes

## Long-term

### Production database
H2 in-memory is development-only. For a persistent deployment:
- Add a PostgreSQL (or MySQL) dependency and connection properties
- Introduce a migration tool (Flyway or Liquibase) to manage schema changes
  (currently `ddl-auto=none` with manual SQL scripts)
- Add a `docker-compose.yml` for local dev with a real database

### CI/CD
- Add a GitHub Actions workflow: `mvn verify` on push/PR
- Add frontend linting (`eslint`) and type-checking (`tsc --noEmit`) to CI
- Build and publish the WAR/JAR artifact

### Search and filtering
- Add a search/filter bar to the person list (name or email substring)
- Server-side: extend `PersonRepository` with a `findByNameContaining` derived query
- SPA: query-param-driven (`?q=`) so the URL is shareable

## Technical debt

| Item | Impact | Blocked on |
|---|---|---|
| WAR packaging | Cannot use `java -jar` cleanly; Tomcat is heavier than needed | JSP removal |
| Two UI layers (JSP + SPA) | Duplicated validation logic, CSS partially shared, navigation inconsistency | Nothing — ready for JSP removal |
| `Page<T>` class | Cannot be a record; getters required for JSP EL | JSP removal |
| No frontend tests | No unit or integration tests for Vue components | — |
| Font Awesome via CDN | External dependency; would fail offline | Low priority |

## Nice-to-have

- Sort the person list by clicking column headers
- Inline edit (edit in-place in the list row)
- Bulk delete (multi-select checkboxes)
- Note character count / remaining indicator
- Dark-mode system preference detection (`prefers-color-scheme` → default theme)
