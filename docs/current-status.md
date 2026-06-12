# Current Status

_Last updated: 2026-06-12_

## Project state

The project has two fully functional UIs sharing one backend. Person CRUD is
complete in both. Notes management is only in the JSP UI; the SPA links back
to the JSP notes page for note actions.

## Completed features

### Backend
- Spring Boot 3.5, Java 25, Maven, WAR packaging
- H2 in-memory database with SQL-script-managed schema (`ddl-auto=none`)
- UUID primary keys for `person` and `note`
- Spring Data JPA / Hibernate 6 with `@SoftDelete` on notes
- `PersonService` and `NoteService` with full CRUD, ownership validation, and pagination
- 142 seed people with ~323 notes (deterministic UUIDv5 keys in `data.sql`)
- 70 passing tests across 9 test classes (`@DataJpaTest` slices + `@SpringBootTest` MockMvc)

### REST API
- `GET/POST/PUT/DELETE /api/persons` — full person CRUD with pagination
- `GET/POST/DELETE /api/persons/{id}/notes` — note list, add, soft-delete
- RFC 9457 `ProblemDetail` error responses (404, 400 with field map)
- Swagger UI at `/swagger-ui/index.html`, OpenAPI at `/v3/api-docs`

### JSP UI (feature-complete)
- Paginated person list with note-count hover icons (Font Awesome)
- Person create/edit/delete with server-side and client-side validation
- Notes page per person: add and soft-delete
- 10 UI themes via `⚙ Settings` menu; `localStorage` persistence; pre-paint script prevents FOUC

### Vue SPA (`/app/`)
- Vue Router with HTML5 history mode under `/app/`
- Person list with pagination, page-size selector, flash messages
- Create and edit person forms with client-side validation (mirrors Bean Validation rules) and server-side field error display
- Delete confirmation view
- Theme picker (`ThemePicker` component) sharing the same `localStorage` key and CSS variables as the JSP UI
- Pre-paint theme script in `index.html` prevents flash
- `ClassicUiLink` component links back to JSP UI
- API client layer with typed `ApiError` from `application/problem+json` responses
- Vite build integrated into Maven via `frontend-maven-plugin`; `src/main/resources/static/app/` output gitignored

## In progress / incomplete

- **Notes management in the SPA** — not implemented. The note icon in `PersonListView` links directly to `/persons/{id}/notes` (the JSP notes page) rather than a SPA route.

## Known issues

- No authentication or CSRF protection on any endpoint.
- The SPA has no dedicated error boundary or 404 page for unknown client-side routes.
- Deleting a person from the SPA while on their edit page will land the user on a broken state (the API returns 404 on the subsequent fetch; the view shows an error message but does not auto-redirect).

## Recent major changes

| Commit | Change |
|---|---|
| `8a93dea` | Person CRUD fully implemented in the SPA (Cursor) |
| `dcdc741` | Theme system ported to the SPA (Cursor) |
| `a43a4ed` | Person list with pagination implemented in the SPA (Cursor) |
| `b516a18` | Vue 3 + TypeScript + Vite wired in as a hello-world SPA |
| `4ec9bc3` | REST API + Swagger UI added |

## Open technical concerns

1. **Security** — no Spring Security; all REST endpoints and JSP pages are
   unauthenticated. Needed before any public-facing deployment.
2. **Two active UI layers** — JSP and Vue SPA coexist; CSS is shared but
   controller logic, client-side validation, and navigation are duplicated.
   The JSP layer is planned for removal once notes are in the SPA.
3. **WAR packaging** — required for JSP support. Switching to JAR (and
   dropping `tomcat-embed-jasper`) is blocked on completing the SPA and
   removing JSP.
4. **H2 in-memory** — suitable for development only; no persistence across
   restarts and no production database story.
5. **`Page<T>` class vs record** — `Page<T>` cannot be a record because JSP
   EL requires `getXxx()` methods. Minor technical debt; resolved when JSP
   is removed.

## Recommended next actions

1. Add notes management to the Vue SPA (see [roadmap.md](roadmap.md)).
2. Remove the JSP layer once the SPA is feature-complete.
3. Switch packaging from WAR to JAR.
4. Add Spring Security (session + CSRF at minimum for a local app).
