# Java Docker

## Build and test
- `docker build -t java-stakeholder .`
- `docker run --rm java-stakeholder --list-values`

## Rationale
- The host environment may not provide Java 25.
- The Docker image uses Java 25 end to end so the build, tests, and packaged runtime match the repo contract.
- The image passes `--enable-native-access=ALL-UNNAMED` explicitly so Jansi works on Java 25 without default runtime warning noise.
- The build stage runs the full Java gate: `spotless:check`, `checkstyle:check`, `spotbugs:check`, `test`, and `package`.
