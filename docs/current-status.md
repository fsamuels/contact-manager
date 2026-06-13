# Current Status

_Last updated: 2026-06-12_

## Project state

The migration from server-rendered JSP to a modern stack is complete. The
application is now a Spring Boot 3.5 REST API with a Vue 3 + TypeScript SPA
as its only UI, packaged as a single executable JAR. The legacy JSP layer
has been removed.

## Completed features

### Backend
- Spring Boot 3.5, Java 25, Maven, executable JAR packaging
- H2 in-memory database with SQL-script-managed schema (`ddl-auto=none`)
- UUID primary keys for `person` and `note`
- Spring Data JPA / Hibernate 6 with `@SoftDelete` on notes
- `PersonService` and `NoteService` with full CRUD, ownership validation, and pagination
- 142 seed people with ~323 notes (deterministic UUIDv5 keys in `data.sql`)
- 51 passing tests across 8 test classes (`@DataJpaTest` slices + `@SpringBootTest` MockMvc)

### REST API
- `GET/POST/PUT/DELETE /api/persons` — full person CRUD with pagination
- `GET/POST/DELETE /api/persons/{id}/notes` — note list, add, soft-delete
- RFC 9457 `ProblemDetail` error responses (404, 400 with field map)
- Swagger UI at `/swagger-ui/index.html`, OpenAPI at `/v3/api-docs`

### Vue SPA (`/app/`) — the only UI
- Vue Router with HTML5 history mode under `/app/`
- Person list with pagination, page-size selector, flash messages
- Create and edit person forms with client-side validation (mirrors Bean Validation rules) and server-side field error display
- Delete confirmation view
- Notes view (`PersonNotesView`): add notes, soft-delete notes, newest first
- 10-theme picker persisted in `localStorage`; pre-paint script in `index.html` prevents flash
- Theme stylesheet (`frontend/src/assets/styles.css`) bundled by Vite
- API client layer with typed `ApiError` from `application/problem+json` responses
- `/` redirects to `/app/`; all client routes forward server-side so deep links and refreshes work
- Vite build integrated into Maven via `frontend-maven-plugin`; `src/main/resources/static/app/` output gitignored

## In progress / incomplete

Nothing in progress. The JSP decommissioning (the last planned migration
step) is done.

## Known issues

- No authentication or CSRF protection on any endpoint.
- The SPA has no dedicated error boundary or 404 page for unknown client-side routes.
- Deleting a person from the SPA while on their edit page will land the user on a broken state (the API returns 404 on the subsequent fetch; the view shows an error message but does not auto-redirect).

## Recent major changes

| Commit | Change |
|---|---|
| _(uncommitted)_ | JSP layer removed: WAR→JAR packaging, `Page<T>` converted to a record, stylesheet moved into the Vite build, `/` redirects to the SPA |
| _(uncommitted)_ | Notes management in the SPA + deep-link forwards in `SpaController` |
| `8a93dea` | Person CRUD fully implemented in the SPA (Cursor) |
| `dcdc741` | Theme system ported to the SPA (Cursor) |
| `a43a4ed` | Person list with pagination implemented in the SPA (Cursor) |
| `b516a18` | Vue 3 + TypeScript + Vite wired in as a hello-world SPA |
| `4ec9bc3` | REST API + Swagger UI added |

## Open technical concerns

1. **Security** — no Spring Security; all endpoints are unauthenticated.
   Needed before any public-facing deployment.
2. **H2 in-memory** — suitable for development only; no persistence across
   restarts and no production database story.
3. **No frontend tests** — the Vue code has no unit or component test
   coverage; the deleted JSP MockMvc tests were replaced only by API-level
   coverage.

## Recommended next actions

1. Add Spring Security (session + CSRF at minimum for a local app).
2. Add a frontend test setup (Vitest + Vue Test Utils).
3. See [roadmap.md](roadmap.md) for the longer-term list.
