# Generation Report

## Model and Environment

| | |
|---|---|
| Model | Claude Fable 5 (`claude-fable-5`) |
| Provider / interface | Anthropic, via Claude Code (CLI / Claude Agent SDK) |
| Date generated | 2026-06-11 |
| Build environment | OpenJDK 26.0.1 (Homebrew, aarch64), Apache Maven 3.9.16, macOS (Darwin 25.5.0 / Mac OS X 26.5.1) |

The code is compiled with `--release 17`, so any JDK 17+ can build and run it.

## Exact Prompt

> Read the project specification in `SPEC.md` and implement the described application from scratch in this directory. Do not ask clarifying questions unless there is a genuine ambiguity or contradiction in the spec — make reasonable decisions and document them.
>
> In addition to the application code, create a `GENERATION.md` file in the project root containing:
>
> - Model name and version
> - Provider and interface used
> - Date generated
> - The exact prompt used
> - Any non-obvious implementation decisions and their reasoning
> - Generation metrics: conversation turns, files created, lines of code, build attempts, compilation errors, and clarifying questions asked
> - Token usage (input, output, total) and estimated cost — if unavailable during generation, mark as `_fill from session log_`
> - Wall-clock session time — if unavailable, mark as `_fill from session log_`
> - Build environment: JDK version, Maven version, OS

## Implementation Decisions

- **Version selection was verified against Maven Central at generation time** rather than recalled from memory. Latest *stable* releases were chosen, deliberately skipping pre-releases (Spring 7.0.0-M6, Jetty 12.1.0.alpha2, JUnit 5.13.0-M3): Spring 6.2.8, Jetty 12.0.21 (EE10), H2 2.3.232, Hibernate Validator 8.0.2.Final, JSTL 3.0 (API 3.0.2 / Glassfish impl 3.0.1), Expressly 5.0.0, JUnit 5.12.2.
- **Jetty 12 EE10 environment** (`jetty-ee10-maven-plugin`) was chosen because Spring MVC 6 targets Jakarta Servlet 6.0 (EE10). The app runs locally with `mvn jetty:run`; the plugin provides the JSP engine.
- **H2 in-memory database** via Spring's `EmbeddedDatabaseBuilder` — the spec allows "in memory, local file, or optionally PostgreSQL"; in-memory keeps the app runnable with zero setup. Schema and seed data live in `src/main/resources/db/`.
- **Seed data**: three sample people are inserted on startup so the listing demonstrates immediately; deleting them all shows the spec-required "No results found" state. (An empty initial DB would also satisfy the spec; this was a usability call.)
- **Validation follows the spec rules exactly.** Notably, no email *format* validation (`@Email`) was added — the spec defines email validation as only "non empty, max 30 characters", and adding stricter rules would reject inputs the spec considers valid. Validation runs on **both** sides: Jakarta Bean Validation annotations on `Person` (authoritative, server) and a jQuery script mirroring the same rules (immediate feedback, client). The client script can be bypassed; the server always re-validates.
- **`Person` doubles as the form-backing object.** For a 7-field CRUD app a separate form/DTO layer would add indirection without benefit; the Javadoc notes the dual role.
- **State abbreviation is normalized to uppercase in the service layer** before persistence. The spec says "exactly 2 letters", so lowercase input is accepted (per spec) but stored canonically.
- **Whitespace-only input fails validation**: a `StringTrimmerEditor` trims bound values and converts empty strings to `null`, so `@NotBlank` rejects `"   "`.
- **No `web.xml`** — servlet setup is programmatic (`AbstractAnnotationConfigDispatcherServletInitializer`), the modern Spring MVC idiom. It also enables `defaultHtmlEscape` and cookie-only session tracking (no `jsessionid` URL rewriting).
- **Deletion is a POST**, not a GET link, from the confirmation page — state-changing actions should not be reachable via GET (crawlers/prefetchers). The confirmation page wording matches the spec exactly.
- **Create and edit share one JSP** (`form.jsp`) that posts back to its own URL, so validation-failure redisplay needs no extra handling and the two workflows stay identical per the spec.
- **Layering**: controller → service (interface + impl, `@Transactional`) → DAO (interface + Spring JDBC impl with `NamedParameterJdbcTemplate`) → H2. Web beans live in the servlet context (`WebMvcConfig`), infrastructure/service/DAO beans in the root context (`RootConfig`).
- **Post/Redirect/Get with flash attributes** for success/error messages, so refreshing the listing never resubmits a form.
- **jQuery 3.7.1 is loaded from the official CDN** with subresource integrity, keeping the build simple (no WebJars). If offline, the page still works — server-side validation is the authority.
- **Tests** (26 total): Bean Validation rules unit tests, DAO integration tests against embedded H2 (transactional, rolled back), and MockMvc tests covering every controller flow including validation failures and the not-found path. Tests use a uniquely named embedded DB without seed data so they never share state with each other or the runtime DB.

## Generation Metrics

| Metric | Value |
|---|---|
| Conversation turns | 1 user prompt; ~18 assistant turns (~35 tool invocations), fully autonomous |
| Clarifying questions asked | 0 |
| Files created | 26 (10 main Java, 4 test Java, 5 JSP/JSPF, 2 SQL, 1 CSS, 1 JS, pom.xml, README.md, GENERATION.md) |
| Lines of code | ~1,833 total (774 main Java, 441 test Java, 156 JSP, 232 CSS/JS, 17 SQL, 213 pom/README) |
| Build attempts | 3 distinct `mvn verify` states until green (5 invocations incl. summary re-runs), plus 2 `mvn jetty:run` smoke-test runs |
| Compilation errors | 1 (Hamcrest missing from test classpath — required by Spring's MockMvc result matchers) |
| Test failures | 5 tests, 1 root cause (Spring 6.1+ requires the `-parameters` compiler flag for `@PathVariable` name resolution); fixed via compiler config + explicit annotation names |
| Runtime verification | Full HTTP smoke test against `mvn jetty:run`: list, create (valid + invalid), edit, delete confirm + delete, not-found redirect, empty-list "No results found", static assets |
| Token usage (input / output / total) | _fill from session log_ |
| Estimated cost | _fill from session log_ |
| Wall-clock session time | _fill from session log_ |

## Post-Generation Revisions

- **2026-06-11** — Removed all company-specific branding ahead of the initial
  commit: Java packages renamed to `com.example.contactmanager`, Maven
  `groupId` changed to `com.example`, project name shortened to
  "Contact Manager", and `SPEC.md`/`README.md` reworded to describe the spec
  generically as an interview-style sample development project. Full build and
  HTTP smoke test re-run after the rename.
