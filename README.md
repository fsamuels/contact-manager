# Contact Manager

A simple CRUD contact manager web application built per [SPEC.md](SPEC.md), an
interview-style sample development project specification.

## Stack

- Java 25
- Maven
- Spring Boot 3.5 (embedded Tomcat; Tomcat is required for Boot's JSP support)
- Spring MVC 6 / Spring JDBC 6
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

The database is in-memory: it is created (with a few sample records) on
startup and discarded on shutdown.

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
| Data access (Spring JDBC) | `com.example.contactmanager.dao` |
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
