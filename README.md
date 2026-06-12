# Contact Manager

A simple CRUD contact manager web application built per [SPEC.md](SPEC.md), an
interview-style sample development project specification.

## Stack

- Java 25
- Maven
- Spring Boot 3.5 (embedded Tomcat; Tomcat is required for Boot's JSP support)
- Spring MVC 6 / Spring Data JPA (Hibernate)
- H2 in-memory database
- JSP (JSTL 3) views with HTML5, CSS, and jQuery
- Jakarta Bean Validation (Hibernate Validator)

## Running locally

Requires JDK 25 or newer and Maven 3.9+.

```bash
mvn spring-boot:run
```

or build and run the executable WAR:

```bash
mvn package && java -jar target/contact-manager.war
```

Then open <http://localhost:8080/>.

The database is in-memory: it is created (with sample people and notes) on
startup and discarded on shutdown.

## Vue frontend (work in progress)

A Vue 3 + TypeScript + Vite SPA lives in `frontend/` and is served by Spring
Boot at `/app/`. The `frontend-maven-plugin` installs Node, runs the Vite
build during `mvn package`, and bundles the output into the artifact — one
deployable, same as before. Skip the frontend build with
`-Dskip.frontend=true`. For frontend development with hot reload, run the
backend normally and `npm run dev` inside `frontend/` (API calls are proxied
to `localhost:8080`). Currently a hello-world page; the full UI migration is
planned.

## REST API

A JSON API mirrors the web UI's functionality, sharing the same service
layer. Interactive documentation (Swagger UI) is served at
`/swagger-ui/index.html`; the OpenAPI document at `/v3/api-docs`.

| Method | Path | Description |
|---|---|---|
| GET | `/api/persons?page=&size=` | Paginated person list (size 1-100, default 10) |
| GET | `/api/persons/{id}` | Single person (includes `noteCount`) |
| POST | `/api/persons` | Create (201 + Location) |
| PUT | `/api/persons/{id}` | Update |
| DELETE | `/api/persons/{id}` | Delete (cascades to notes) |
| GET | `/api/persons/{personId}/notes` | A person's notes, newest first |
| POST | `/api/persons/{personId}/notes` | Add a note (201 + Location) |
| DELETE | `/api/persons/{personId}/notes/{noteId}` | Soft-delete a note (204) |

Errors are RFC 9457 problem details (`application/problem+json`): 404 for
missing records, 400 with a field-to-message `errors` map for validation
failures.

## Notes

Each person can have any number of free-text notes, managed from the
note icon in the listing's Actions column (the hover text shows the current
note count). Notes can only be added and deleted — deletion is a soft delete
(Hibernate `@SoftDelete`): the row is kept with a `deleted` flag and filtered
out of every query. Action links use Font Awesome icons with accessible
labels.

## UI themes

The &#9881; Settings menu in the top bar switches the UI between themes:
Light Mode (default), Dark Mode, Google, Claude, Facebook, Alaska Airlines,
Reddit, Yahoo, Wikipedia, and Amazon. Each theme adjusts colors, fonts, and
design elements via CSS
custom properties (the `[data-theme]` blocks in `resources/css/styles.css`).
The selection is saved per browser in `localStorage` and re-applied before
first paint by an inline script in `header.jspf`.

## Building and testing
 compile, run tests, package the WAR
```bash
mvn verify
```

The WAR is produced at `target/contact-manager.war`.

## Application structure

| Layer | Package / location |
|---|---|
| Application entry point | `com.example.contactmanager.ContactManagerApplication` |
| Web (controllers) | `com.example.contactmanager.web` |
| Service | `com.example.contactmanager.service` |
| Data access (Spring Data JPA) | `com.example.contactmanager.repository` |
| Domain model | `com.example.contactmanager.domain` |
| Configuration | `src/main/resources/application.properties` |
| Views (JSP) | `src/main/webapp/WEB-INF/views` |
| Static assets (CSS/JS) | `src/main/webapp/resources` |
| DB schema / seed data | `src/main/resources/db` |

## Validation

All fields are validated server-side with Bean Validation (authoritative) and
client-side with jQuery (immediate feedback):

| Field | Rule |
|---|---|
| First name | non-empty, max 30 characters |
| Last name | non-empty, max 30 characters |
| Email address | non-empty, max 30 characters |
| Street address | non-empty, max 60 characters |
| City | non-empty, max 30 characters |
| State | exactly 2 letters (stored uppercase) |
| Zip code | exactly 5 digits |
