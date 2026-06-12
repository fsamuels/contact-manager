# Contact Manager

A CRUD contact manager with two UIs sharing the same backend: a classic JSP
UI and a modern Vue 3 SPA — both served from the same Spring Boot executable
artifact.

See [docs/architecture.md](docs/architecture.md),
[docs/current-status.md](docs/current-status.md), and
[docs/roadmap.md](docs/roadmap.md) for more detail.

## Features

- **Person management** — create, edit, delete, paginated list (10/25/50/100 per page)
- **Notes** — each person can have any number of free-text notes; add and soft-delete only
- **Two UIs** — Vue 3 SPA at `/app/` and classic JSP UI at `/persons` (both support full person CRUD; notes management is currently JSP-only)
- **REST API** — full CRUD, RFC 9457 problem details, Swagger UI
- **10 UI themes** — Light, Dark, Google, Claude, Facebook, Alaska Airlines, Reddit, Yahoo, Wikipedia, Amazon; persisted in `localStorage`
- **142 seed records** with ~323 notes pre-loaded on every startup

## Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Build | Maven 3.9+ |
| Framework | Spring Boot 3.5 (embedded Tomcat) |
| Persistence | Spring Data JPA / Hibernate 6, H2 in-memory |
| Legacy UI | Spring MVC, JSP (JSTL 3), jQuery |
| SPA | Vue 3, TypeScript, Vite 6, Vue Router 4 |
| API docs | springdoc-openapi 2 (Swagger UI) |
| Validation | Jakarta Bean Validation (Hibernate Validator) |
| Testing | JUnit 5, Spring Boot Test, MockMvc |

## Running locally

Requires JDK 25+ and Maven 3.9+.

```bash
mvn spring-boot:run
```

Or build and run the executable WAR:

```bash
mvn package && java -jar target/contact-manager.war
```

The database is in-memory: schema and seed data are applied on every startup
and discarded on shutdown.

| URL | Description |
|---|---|
| `http://localhost:8080/` | Redirects to JSP person list |
| `http://localhost:8080/persons` | JSP person list |
| `http://localhost:8080/app/` | Vue SPA person list |
| `http://localhost:8080/swagger-ui/index.html` | Swagger UI |
| `http://localhost:8080/v3/api-docs` | OpenAPI document |

## Frontend development (hot reload)

Run the backend normally, then in a separate terminal:

```bash
cd frontend
npm install   # first time only
npm run dev
```

The Vite dev server starts on `http://localhost:5173`. It proxies `/api` and
`/resources` to the Boot instance on port 8080, so the SPA gets live API data
with instant hot-module reload.

To skip the Vite build during Maven runs (backend-only work):

```bash
mvn spring-boot:run -Dskip.frontend=true
```

## Testing

```bash
mvn test -Dskip.frontend=true   # fast: skips Vite build, runs all 70 Java tests
mvn verify                       # full build including frontend
```

Tests run against an empty in-memory database (seed data is not loaded); each
test is transactional and rolls back.

## REST API

Errors are RFC 9457 problem details (`application/problem+json`): 404 for
missing records, 400 with a field-to-message `errors` map for validation
failures.

| Method | Path | Description |
|---|---|---|
| GET | `/api/persons?page=&size=` | Paginated list (size 1–100, default 10) |
| GET | `/api/persons/{id}` | Single person (includes `noteCount`) |
| POST | `/api/persons` | Create (201 + Location) |
| PUT | `/api/persons/{id}` | Update |
| DELETE | `/api/persons/{id}` | Delete, cascades to notes (204) |
| GET | `/api/persons/{personId}/notes` | Person's notes, newest first |
| POST | `/api/persons/{personId}/notes` | Add a note (201 + Location) |
| DELETE | `/api/persons/{personId}/notes/{noteId}` | Soft-delete a note (204) |

## Notes

Each person can have any number of free-text notes. Notes can only be added
and deleted — deletion is a soft delete (Hibernate `@SoftDelete`): the row is
kept with a `deleted` flag and filtered out of every query. The note count
appears on hover over the note icon in the Actions column.

## UI themes

The ⚙ Settings menu in the top bar switches between 10 themes. The selection
is persisted in `localStorage` and applied before first paint by an inline
script — no flash of unstyled content. The same theme system works in both
the JSP UI and the Vue SPA via shared CSS custom properties on `html[data-theme]`.

## Building and testing

Compile, run tests, and package the WAR:

```bash
mvn verify
```

The WAR is produced at `target/contact-manager.war`.

## Application structure

```
contact-manager/
├── frontend/                       # Vue 3 SPA source
│   ├── src/
│   │   ├── api/                    # REST client (http.ts, persons.ts)
│   │   ├── components/             # PersonForm, ThemePicker, ClassicUiLink
│   │   ├── composables/            # useTheme
│   │   ├── constants/              # themes.ts
│   │   ├── router/                 # Vue Router (index.ts)
│   │   ├── types/                  # TypeScript types (api.ts, person.ts)
│   │   ├── utils/                  # personValidation.ts
│   │   ├── views/                  # PersonListView, PersonFormView, PersonDeleteView
│   │   ├── App.vue                 # Root component (header + RouterView)
│   │   └── main.ts                 # App bootstrap
│   ├── index.html                  # SPA entry (pre-paint theme script)
│   ├── package.json
│   └── vite.config.ts
├── src/
│   ├── main/
│   │   ├── java/com/example/contactmanager/
│   │   │   ├── api/                # REST controllers, DTOs, error advice
│   │   │   ├── domain/             # Person, Note, Page<T>
│   │   │   ├── repository/         # PersonRepository, NoteRepository
│   │   │   ├── service/            # PersonService, NoteService + impls
│   │   │   └── web/                # JSP controllers + SpaController
│   │   ├── resources/
│   │   │   ├── application.properties
│   │   │   ├── db/                 # schema.sql, data.sql (142 people + ~323 notes)
│   │   │   └── static/app/         # Vite build output (gitignored, generated)
│   │   └── webapp/WEB-INF/views/   # JSP templates, shared CSS/JS
│   └── test/                       # 70 tests across 9 test classes
├── docs/
│   ├── architecture.md
│   ├── current-status.md
│   └── roadmap.md
├── pom.xml
└── README.md
```

## Field validation

All fields are validated server-side (Bean Validation, authoritative) and
client-side (jQuery in JSP UI; TypeScript in SPA):

| Field | Rule |
|---|---|
| First name | Non-empty, max 30 characters |
| Last name | Non-empty, max 30 characters |
| Email address | Non-empty, max 30 characters |
| Street address | Non-empty, max 60 characters |
| City | Non-empty, max 30 characters |
| State | Exactly 2 letters (stored uppercase) |
| Zip code | Exactly 5 digits |
