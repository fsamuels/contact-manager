# Contact Manager

A simple CRUD contact manager web application built per [SPEC.md](SPEC.md), an
interview-style sample development project specification.

## Stack

- Java 17+ (compiled with `--release 17`)
- Maven
- Jetty 12 (EE10) via `jetty-ee10-maven-plugin`
- Spring MVC 6 / Spring JDBC 6
- H2 in-memory database
- JSP (JSTL 3) views with HTML5, CSS, and jQuery
- Jakarta Bean Validation (Hibernate Validator)

## Running locally

Requires JDK 17 or newer and Maven 3.9+.

```bash
mvn jetty:run
```

Then open <http://localhost:8080/>.

The database is in-memory: it is created (with a few sample records) on
startup and discarded on shutdown.

## Building and testing
 compile, run tests, package the WAR
```bash
mvn verify
```

The WAR is produced at `target/contact-manager.war`.

## Application structure

| Layer | Package / location |
|---|---|
| Web (controllers) | `com.example.contactmanager.web` |
| Service | `com.example.contactmanager.service` |
| Data access (Spring JDBC) | `com.example.contactmanager.dao` |
| Domain model | `com.example.contactmanager.domain` |
| Configuration | `com.example.contactmanager.config` |
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
