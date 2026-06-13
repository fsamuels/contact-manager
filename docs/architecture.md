# Architecture

## Overview

Contact Manager is a Spring Boot 3.5 application exposing a JSON REST API,
with a Vue 3 + TypeScript single-page application as its UI. Both ship in a
single executable JAR: the Vite-built SPA is bundled into Boot's static
resources and served at `/app/`.

```
Browser ── /app/ (Vue SPA, Vue Router)
              │
              ▼  fetch
        REST API (/api/persons, /api/persons/{id}/notes)
              │
        Service layer
              │
        Repository layer (Spring Data JPA / Hibernate)
              │
           H2 (in-memory)
```

## Major components

### Backend

| Package | Responsibility |
|---|---|
| `domain` | JPA entities (`Person`, `Note`) and the `Page<T>` paging record |
| `repository` | Spring Data JPA interfaces; `NoteRepository` has a JPQL group-by query for batch note counts |
| `service` | Business logic: pagination clamping, state-abbreviation normalisation, note ownership checks |
| `web` | `SpaController`: redirects `/` to the SPA and forwards client-side routes to `index.html` |
| `api` | REST controllers (`PersonApiController`, `NoteApiController`), record DTOs, `ApiExceptionHandler` |

**`ApiExceptionHandler`** (`@RestControllerAdvice` scoped to the `api`
package) converts exceptions to RFC 9457 problem details: 404 for missing
records, 400 with a field-to-message `errors` map for validation failures.

### Frontend (`frontend/`)

| Path | Responsibility |
|---|---|
| `src/api/` | Thin fetch wrappers (`persons.ts`, `notes.ts`); `parseJsonResponse` in `http.ts` converts non-OK responses and `application/problem+json` bodies into typed `ApiError` exceptions |
| `src/assets/styles.css` | The design system: CSS custom properties with 10 `html[data-theme]` blocks; bundled by Vite |
| `src/types/` | TypeScript interfaces mirroring the Java DTOs (`PersonDto`, `NoteDto`, `PageDto`, `ProblemDetail`) |
| `src/utils/personValidation.ts` | Client-side field validation logic (mirrors Bean Validation rules) |
| `src/composables/useTheme.ts` | Module-level reactive theme state; reads/writes `localStorage` and `html[data-theme]` |
| `src/constants/themes.ts` | Single source of truth for the 10 theme IDs and labels |
| `src/components/` | Reusable components: `PersonForm` (controlled form with blur-validation), `ThemePicker` (dropdown menu) |
| `src/views/` | Route-level components: `PersonListView`, `PersonFormView` (create + edit), `PersonDeleteView`, `PersonNotesView` |
| `src/router/index.ts` | Vue Router with `createWebHistory('/app/')` |

## Data model

```sql
person (person_id UUID PK, first_name, last_name, email_address,
        street_address, city, state CHAR(2), zip_code CHAR(5))

note   (note_id UUID PK, person_id UUID FK → person,
        note_text VARCHAR(1000), created_at TIMESTAMP,
        deleted BOOLEAN DEFAULT FALSE)
```

- Primary keys are UUIDs assigned by the application (`@UuidGenerator`);
  seed data uses deterministic UUIDv5 values.
- Notes use Hibernate `@SoftDelete(columnName="deleted")`: `delete()` becomes
  `UPDATE SET deleted=TRUE` and Hibernate automatically adds `WHERE deleted=FALSE`
  to every query.
- Hard-deleting a person cascades to their notes via `ON DELETE CASCADE`.

## Data flow — typical request

```
PersonListView → fetchPersons(page, size, sort, direction)
  → GET /api/persons?page=1&size=20&sort=lastName&direction=asc
  → PersonApiController.list()
  → personService.listPeople() + noteService.countNotes()
  → PersonRepository (Spring Data JPA) → H2
  ← PageDto<PersonDto> (JSON)
  ← PersonListView renders table + pagination
```

## Deployment architecture

`mvn package` produces a single executable `target/contact-manager.jar`
(embedded Tomcat, `java -jar`).

The Vite build (`frontend-maven-plugin` → `vite build`) runs during the
`generate-resources` Maven phase and outputs into
`src/main/resources/static/app/`. Spring Boot's static resource handling
serves that directory at `/app/**`. `SpaController` redirects `/` to `/app/`
and forwards each client-side route to `/app/index.html` so deep links and
refreshes work.

## Frontend build integration

```
mvn package
  └── generate-resources phase
        ├── install-node-and-npm  (Node v22.12.0 into frontend/node/)
        ├── npm install           (into frontend/node_modules/)
        └── npm run build         (vite build → static/app/)
```

Skip with `-Dskip.frontend=true`. For dev, run `npm run dev` in `frontend/`;
Vite proxies `/api` to `localhost:8080` with hot-module reload.

## Design decisions

| Decision | Rationale |
|---|---|
| `ddl-auto=none` | SQL scripts in `db/schema.sql` are the authoritative schema definition; Hibernate never auto-generates or alters tables |
| `open-in-view=false` | Prevents lazy-load surprises outside the service layer; all data is loaded before serialization |
| REST error advice scoped to `api` package | Keeps the JSON problem-detail contract explicit to the API layer |
| `@SoftDelete` on `Note` | Notes are never physically removed; the flag is transparent to all queries via Hibernate's filter |
| UUID primary keys | Avoids integer-sequence collisions in multi-instance scenarios; deterministic UUIDv5 in seed data ensures consistent FK relationships |
| SPA route forwards enumerated in `SpaController` | Each client route is listed explicitly rather than a `/app/**` catch-all, so requests for real files (hashed assets) still reach the static resource handler |
| Pre-paint theme script in `index.html` | Applies the persisted theme to `html[data-theme]` before the stylesheet loads, preventing a flash of the default theme |

### History

The project began as a server-rendered JSP application and was migrated
incrementally: Spring Boot → Hibernate/JPA → UUID keys → REST API → Vue SPA.
The JSP layer was removed once the SPA reached feature parity, which also
allowed the switch from WAR to JAR packaging and converting `Page<T>` to a
record (JSP EL required `getXxx()` accessors).

## Technical constraints

- **H2 in-memory database** — data is lost on restart; the SQL init scripts
  run on every boot. Tests override `spring.sql.init.data-locations` to start
  empty.
- **Node version pinned** in `pom.xml` (`frontend-maven-plugin` installs
  v22.12.0); local `npm run dev` can use any compatible Node.

## Known architectural debt

- **No authentication or CSRF protection** — all endpoints are
  unauthenticated. Spring Security is needed before any public-facing
  deployment.
- **No production database story** — H2 in-memory only; a real database plus
  a migration tool (Flyway/Liquibase) is future work.
- **No frontend test suite** — the Vue code has no unit or component tests;
  coverage is backend-only.
- **Font Awesome via CDN** — the icon font is an external runtime dependency
  of `frontend/index.html`.
