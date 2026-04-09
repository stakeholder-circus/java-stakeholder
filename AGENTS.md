# java-stakeholder AGENTS

1. Java is first-class target and has priority over all scaffolds.
2. Use Java 25 and Maven Wrapper.
3. Run:
   - `./mvnw -q test`
   - `./mvnw -q package`
4. Reuse shared contract via relative submodule `core/`.
5. Must support: `--dev-type`, `--jargon`, `--complexity`, `--duration`, `--alerts`, `--project`, `--minimal`, `--team`, `--framework`.
6. Add non-breaking extras `--seed`, `--output-format`, `--no-color`, `--trace`, `--list-values`.
7. Implement deterministic mode and normalized JSON output.
8. For unimplemented features, fail fast with explicit error and log in `GAPS.md`.
9. Keep architecture CLEAN and testable. Domain logic pure where possible.
