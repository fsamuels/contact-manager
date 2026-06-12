# Architecture

## Overview

Contact Manager is a Spring Boot 3.5 application that serves two complete UIs
from a single executable artifact:

1. **JSP UI** (`/persons`) — classic server-rendered views using Spring MVC,
   JSP/JSTL, jQuery, and CSS custom properties.
2. **Vue SPA** (`/app/`) — a client-side application built with Vue 3,
   TypeScript, and Vite Router, consuming the REST API.

Both UIs share the same service layer. The REST API is the sole data access
path for the SPA; the JSP controllers call the service layer directly.

```
Browser
  ├── /persons (JSP UI)
  │     └── PersonController / NoteController
  │                 │
  │         Service layer
  │                 │
  │         Repository layer (Spring Data JPA)
  │                 │
  │              H2 (in-memory)
  │
  └── /app/ (Vue SPA)
        └── REST API (/api/persons, /api/persons/{id}/notes)
                  │
          Service layer  ←── same beans
                  │
          Repository layer
                  │
               H2 (in-memory)
```

## Major components

### Backend

| Package | Responsibility |
|---|---|
| `domain` | JPA entities (`Person`, `Note`) and the `Page<T>` paging wrapper |
| `repository` | Spring Data JPA interfaces; `NoteRepository` has a JPQL group-by query for batch note counts |
| `service` | Business logic: pagination clamping, state-abbreviation normalisation, note ownership checks |
| `web` | JSP controllers (`PersonController`, `NoteController`) and `SpaController` (SPA entry-point forward) |
| `api` | REST controllers (`PersonApiController`, `NoteApiController`), record DTOs, `ApiExceptionHandler` |

**`ApiExceptionHandler`** is scoped to `basePackages = "com.example.contactmanager.api"` so it only intercepts exceptions in the REST layer; the JSP controllers use redirect-with-flash instead of JSON error responses.

### Frontend (`frontend/`)

| Path | Responsibility |
|---|---|
| `src/api/` | Thin fetch wrappers; `parseJsonResponse` in `http.ts` converts non-OK responses and `application/problem+json` bodies into typed `ApiError` exceptions |
| `src/types/` | TypeScript interfaces mirroring the Java DTOs (`PersonDto`, `PageDto`, `ProblemDetail`) |
| `src/utils/personValidation.ts` | Client-side field validation logic (mirrors Bean Validation rules) |
| `src/composables/useTheme.ts` | Module-level reactive theme state; reads/writes `localStorage` and `html[data-theme]` |
| `src/constants/themes.ts` | Single source of truth for the 10 theme IDs and labels |
| `src/components/` | Reusable components: `PersonForm` (controlled form with blur-validation), `ThemePicker` (dropdown menu), `ClassicUiLink` (back-link to JSP UI) |
| `src/views/` | Route-level components: `PersonListView`, `PersonFormView` (create + edit), `PersonDeleteView` |
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

## Data flow — Vue SPA request

```
PersonListView → fetchPersons(page, size)
  → GET /api/persons?page=1&size=10
  → PersonApiController.list()
  → personService.listPeople() + noteService.countNotes()
  → PersonRepository (Spring Data JPA) → H2
  ← PageDto<PersonDto> (JSON)
  ← PersonListView renders table + pagination
```

## Deployment architecture

A single `contact-manager.war` is produced by `mvn package`. It is executable
(`java -jar`) via Spring Boot's embedded Tomcat launcher and can also be
deployed to a standalone Tomcat container.

The Vite build (`frontend-maven-plugin` → `vite build`) runs during the
`generate-resources` Maven phase and outputs into
`src/main/resources/static/app/`. Spring Boot's static resource handling
serves that directory at `/app/**`. `SpaController` forwards bare `/app` and
`/app/` requests to `/app/index.html` to bootstrap the SPA.

WAR packaging is required because JSP compilation via Jasper is only
supported by Spring Boot with WAR packaging (not JAR).

## Frontend build integration

```
mvn package
  └── generate-resources phase
        ├── install-node-and-npm  (Node v22.12.0 into frontend/node/)
        ├── npm install           (into frontend/node_modules/)
        └── npm run build         (vite build → static/app/)
```

Skip with `-Dskip.frontend=true`. For dev, run `npm run dev` in `frontend/`;
Vite proxies `/api` and `/resources` to `localhost:8080`.

## Design decisions

| Decision | Rationale |
|---|---|
| `ddl-auto=none` | SQL scripts in `db/schema.sql` are the authoritative schema definition; Hibernate never auto-generates or alters tables |
| `open-in-view=false` | Prevents lazy-load surprises in view rendering; all data is loaded in the service layer |
| REST error advice scoped to `api` package | Keeps JSON problem-detail responses isolated from the JSP layer, which uses redirect-with-flash for errors |
| `Page<T>` as a class (not a record) | Jakarta EL 5.0 in JSP requires `getXxx()` accessor methods; records use component accessor names without `get` prefix, which JSP EL cannot invoke |
| `@SoftDelete` on `Note` | Notes are never physically removed; the flag is transparent to all queries via Hibernate's filter |
| UUID primary keys | Avoids integer-sequence collisions in multi-instance scenarios; deterministic UUIDv5 in seed data ensures consistent FK relationships |
| Two UIs coexisting | Allows incremental migration from JSP to Vue without a big-bang rewrite; the JSP UI is removed once the SPA is feature-complete |

## Known architectural debt

- **WAR packaging** required until the JSP layer is removed; after that the project can switch to a JAR and drop `tomcat-embed-jasper`.
- **Two UI layers** — JSP and Vue SPA — both active; they share CSS (via `/resources/css/styles.css`) but have separate navigation and some duplicated logic (client-side validation, note counts).
- **No authentication or CSRF protection** — the app has no security layer. All endpoints are unauthenticated. Before any public-facing deployment, Spring Security is needed.
- **H2 in-memory database** — data is lost on restart; no persistence story for production.
- **`Page<T>` cannot be a record** — a minor design constraint imposed by JSP EL; once JSP is removed, `Page<T>` can be converted to a record.
