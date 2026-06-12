# AQUENT Contact Manager

A simple CRUD contact manager web application: list, create, edit, and delete people.

Built with **Spring MVC**, **Spring JDBC**, **JSP/JSTL**, **jQuery**, and an in-memory **H2**
database, served by **Jetty 12**.

## Requirements

- JDK 21 or newer
- Maven 3.9+

## Running locally

```bash
mvn jetty:run
```

Then open <http://localhost:8080/>. The root redirects to the person listing at `/people`.

The application uses an in-memory H2 database that is created fresh on each startup and seeded
with a few sample people (see `src/main/resources/data.sql`). No external database setup is needed.

## Building

```bash
mvn clean package      # produces target/contact-manager.war
mvn test               # runs the DAO integration tests
```

## Functionality

| Action | URL | Notes |
| ------ | --- | ----- |
| List people | `GET /people` | Shows first name, last name, email; "No results found" when empty |
| Create | `GET /people/create`, `POST /people/create` | Validated form |
| Edit | `GET /people/{id}/edit`, `POST /people/{id}/edit` | Same fields/validation as create |
| Delete | `GET /people/{id}/delete`, `POST /people/{id}/delete` | Confirmation page before deletion |

### Validation rules

| Field | Rule |
| ----- | ---- |
| First name | required, max 30 chars |
| Last name | required, max 30 chars |
| Email address | required, max 30 chars |
| Street address | required, max 60 chars |
| City | required, max 30 chars |
| State | exactly 2 letters |
| Zip code | exactly 5 digits |

Validation runs on both the client (jQuery, immediate feedback) and the server (Jakarta Bean
Validation, authoritative). Server-side error messages identify the failing field and the reason.

## Architecture

Standard layered design:

```
controller  -> service      -> dao          -> H2 (Spring JDBC)
PersonController  PersonService   PersonDao
                                  JdbcPersonDao
```

- `model/Person` — domain object carrying Bean Validation constraints.
- `dao/` — `PersonDao` interface + `JdbcPersonDao` (parameterized SQL via `NamedParameterJdbcTemplate`).
- `service/` — `PersonService` interface + transactional implementation.
- `controller/PersonController` — CRUD workflows using the Post/Redirect/Get pattern.
- `config/` — Java-based configuration (no `web.xml`): `WebAppInitializer`, `WebConfig`, `DataConfig`.
- `webapp/WEB-INF/views/` — JSP views; `webapp/resources/` — static CSS/JS.
