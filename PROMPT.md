# Project Generation Prompt

Use this prompt verbatim when running the generation test across different models.

---

## Prompt

Read the project specification in `SPEC.md` and implement the described application from scratch in this directory. Do not ask clarifying questions unless there is a genuine ambiguity or contradiction in the spec — make reasonable decisions and document them.

In addition to the application code, create a `GENERATION.md` file in the project root containing:

- Model name and version
- Provider and interface used
- Date generated
- The exact prompt used
- Any non-obvious implementation decisions and their reasoning
- Generation metrics: conversation turns, files created, lines of code, build attempts, compilation errors, and clarifying questions asked
- Token usage (input, output, total) and estimated cost — if unavailable during generation, mark as `_fill from session log_`
- Wall-clock session time — if unavailable, mark as `_fill from session log_`
- Build environment: JDK version, Maven version, OS
