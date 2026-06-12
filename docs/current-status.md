# Current Status

_Last updated: 2026-06-12_

## Project state

The project has two feature-complete UIs sharing one backend: the Vue SPA at
`/app/` and the classic JSP UI at `/persons`. The SPA now covers everything
the JSP UI does (person CRUD, notes, themes), so the JSP layer is ready for
decommissioning.

## Completed features

### Backend
- Spring Boot 3.5, Java 25, Maven, WAR packaging
- H2 in-memory database with SQL-script-managed schema (`ddl-auto=none`)
- UUID primary keys for `person` and `note`
- Spring Data JPA / Hibernate 6 with `@SoftDelete` on notes
- `PersonService` and `NoteService` with full CRUD, ownership validation, and pagination
- 142 seed people with ~323 notes (deterministic UUIDv5 keys in `data.sql`)
- 71 passing tests across 10 test classes (`@DataJpaTest` slices + `@SpringBootTest` MockMvc)

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

### Vue SPA (`/app/`) — feature-complete
- Vue Router with HTML5 history mode under `/app/`
- Person list with pagination, page-size selector, flash messages
- Create and edit person forms with client-side validation (mirrors Bean Validation rules) and server-side field error display
- Delete confirmation view
- Notes view (`PersonNotesView`): add notes, soft-delete notes, newest first
- Theme picker (`ThemePicker` component) sharing the same `localStorage` key and CSS variables as the JSP UI
- Pre-paint theme script in `index.html` prevents flash
- `ClassicUiLink` component links back to JSP UI
- API client layer with typed `ApiError` from `application/problem+json` responses
- Server-side deep-link support: `SpaController` forwards all SPA routes to `index.html`, so refresh and direct navigation work
- Vite build integrated into Maven via `frontend-maven-plugin`; `src/main/resources/static/app/` output gitignored

## In progress / incomplete

Nothing — the SPA reached feature parity with the JSP UI. The next phase is
decommissioning the JSP layer (see [roadmap.md](roadmap.md)).

## Known issues

- No authentication or CSRF protection on any endpoint.
- The SPA has no dedicated error boundary or 404 page for unknown client-side routes.
- Deleting a person from the SPA while on their edit page will land the user on a broken state (the API returns 404 on the subsequent fetch; the view shows an error message but does not auto-redirect).
- The application root `/` still redirects to the JSP person list; it should point at the SPA once the JSP layer is removed.

## Recent major changes

| Commit | Change |
|---|---|
| _(uncommitted)_ | Notes management in the SPA + deep-link forwards in `SpaController` |
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
   The SPA is now at feature parity, so the JSP layer can be removed.
3. **WAR packaging** — required for JSP support. Switching to JAR (and
   dropping `tomcat-embed-jasper`) is unblocked once JSP is removed.
4. **H2 in-memory** — suitable for development only; no persistence across
   restarts and no production database story.
5. **`Page<T>` class vs record** — `Page<T>` cannot be a record because JSP
   EL requires `getXxx()` methods. Minor technical debt; resolved when JSP
   is removed.

## Recommended next actions

1. Remove the JSP layer — the SPA is feature-complete (see [roadmap.md](roadmap.md)).
2. Switch packaging from WAR to JAR and repoint `/` at the SPA.
3. Add Spring Security (session + CSRF at minimum for a local app).
