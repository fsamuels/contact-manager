# Generation Report

## Model & Environment

- **Model:** Claude Opus 4.8 (model ID `claude-opus-4-8`)
- **Provider / interface:** Anthropic — Claude Code CLI (Claude Agent SDK)
- **Date generated:** 2026-06-11

## Build environment

- **JDK:** OpenJDK 26.0.1 (Homebrew) — compiler `--release 21` target
- **Maven:** Apache Maven 3.9.16
- **OS:** macOS 26.5.1 (Darwin 25.5.0, arm64)

> Note: the JDK was installed via Homebrew but not on the default `PATH`. The build was run with
> `JAVA_HOME=/opt/homebrew/opt/openjdk/libexec/openjdk.jdk/Contents/Home`.

## Exact prompt used

> Read the project specification in `SPEC.md` and implement the described application from scratch
> in this directory. Do not ask clarifying questions unless there is a genuine ambiguity or
> contradiction in the spec — make reasonable decisions and document them.
>
> In addition to the application code, create a `GENERATION.md` file in the project root containing:
> - Model name and version
> - Provider and interface used
> - Date generated
> - The exact prompt used
> - Any non-obvious implementation decisions and their reasoning
> - Generation metrics: conversation turns, files created, lines of code, build attempts,
>   compilation errors, and clarifying questions asked
> - Token usage (input, output, total) and estimated cost — if unavailable during generation,
>   mark as `_fill from session log_`
> - Wall-clock session time — if unavailable, mark as `_fill from session log_`
> - Build environment: JDK version, Maven version, OS

## Technology choices (mapping the spec to concrete versions)

| Spec requirement | Choice | Version |
| ---------------- | ------ | ------- |
| Jetty (latest stable) | `jetty-ee10-maven-plugin` | 12.0.21 |
| Maven (latest stable) | Maven | 3.9.16 |
| Spring MVC | `spring-webmvc` | 6.2.8 |
| Spring JDBC | `spring-jdbc` | 6.2.8 |
| SQL database (in-memory) | H2 | 2.3.232 |
| JSP views | JSP + JSTL (Jakarta) | JSTL API 3.0.0 / Glassfish impl 3.0.1 |
| HTML5, CSS, jQuery | jQuery (CDN) | 3.7.1 |
| Bean Validation | Hibernate Validator | 8.0.2.Final |

## Non-obvious implementation decisions

1. **Jakarta namespace throughout (Jetty 12 EE10 + Spring 6).** The latest stable Jetty (12.x) and
   Spring (6.x) both require the `jakarta.*` servlet namespace rather than the legacy `javax.*`.
   This dictated Jetty's EE10 module set, the `jetty-ee10-maven-plugin`, JSTL 3.0, and the
   `jakarta.tags.core` taglib URI (which replaces the old `http://java.sun.com/jsp/jstl/core`).

2. **`--release 21` while compiling with JDK 26.** The only JDK available on the machine was
   OpenJDK 26 (very new). Spring 6.2's tested baseline is Java 17–23, so the compiler targets the
   Java 21 LTS bytecode level for portability and to stay within Spring's supported range, while
   still building cleanly under JDK 26.

3. **In-memory H2.** The spec explicitly permits in-memory, local-file, or PostgreSQL. In-memory H2
   was chosen so the project is fully self-contained and runnable with a single `mvn jetty:run`,
   with no external database to install. Schema and seed data live in `schema.sql` / `data.sql`
   and are applied on startup via Spring's `EmbeddedDatabaseBuilder`.

4. **No `web.xml` — Java configuration.** Configuration is done programmatically via
   `AbstractAnnotationConfigDispatcherServletInitializer` (`WebAppInitializer`), which is the modern,
   type-safe Servlet 3+ approach and keeps all wiring in one place.

5. **Validation on both client and server.** The spec allows client, server, or both. Both are
   implemented: Jakarta Bean Validation annotations on the `Person` model are the authoritative
   server-side check (with field-specific messages), and `validation.js` mirrors the same rules in
   jQuery for immediate feedback. HTML5 `maxlength`/`required` attributes provide a third, native layer.

6. **Post/Redirect/Get for all mutations.** Create, edit, and delete each POST and then redirect to
   the listing, preventing duplicate submissions on browser refresh and satisfying the spec's
   "return to the main page" requirement.

7. **Two endpoints per destructive/edit action (GET form + POST action).** Delete uses a dedicated
   confirmation page (`GET /people/{id}/delete`) with the exact wording from the spec, and a separate
   `POST` to perform the deletion — POST is used so deletion is not triggered by a plain link/GET.

8. **Layered structure with interfaces.** `PersonDao` and `PersonService` are interfaces with
   concrete implementations, modeling a production-style codebase where persistence or business
   logic could be swapped or mocked. SQL is fully parameterized to avoid injection.

9. **Missing-record handling.** Editing or deleting a non-existent id redirects back to the listing
   with a flash message rather than throwing, avoiding a raw error page for stale links.

## Generation metrics

| Metric | Value |
| ------ | ----- |
| Conversation turns (assistant tool/response cycles) | ~16 |
| Files created | 22 (19 source/config + README + GENERATION + this report's siblings) |
| Source files (java/jsp/css/js/sql/xml) | 19 |
| Lines of code (those source files, incl. pom) | ~1,335 |
| Java LOC | 739 |
| JSP LOC | 170 |
| Build attempts | 2 (`mvn test`, then `mvn package`) |
| Compilation errors encountered | 0 |
| Test results | 4 DAO integration tests, all passing |
| Runtime verification | Started Jetty, exercised list/create/edit/delete + validation via curl — all passed |
| Clarifying questions asked | 0 (spec was unambiguous) |

## Token usage & timing

- **Token usage (input / output / total):** _fill from session log_
- **Estimated cost:** _fill from session log_
- **Wall-clock session time:** ~8 minutes (first action 15:54:25, build/verify complete ~16:02 local time)

## Verification performed

- `mvn clean test` → BUILD SUCCESS, 4/4 tests pass.
- `mvn jetty:run` → server starts on :8080.
- Manual HTTP checks via `curl`:
  - `GET /` redirects (302) to `/people`.
  - `GET /people` lists seeded people; renders JSP correctly.
  - `GET /people/create` renders the form.
  - `POST /people/create` with invalid data redisplays the form with field-level error messages
    (empty first name, non-2-letter state, non-5-digit zip).
  - `POST /people/create` with valid data persists and redirects.
  - `GET /people/{id}/edit` pre-populates the form; `POST` persists the change (verified city update).
  - `GET /people/{id}/delete` shows the confirmation page with the exact spec wording.
  - `POST /people/{id}/delete` removes the record and redirects.
  - Static CSS/JS served with correct content types.
- `mvn package` → produces `target/contact-manager.war`.
