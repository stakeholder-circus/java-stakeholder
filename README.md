> [!IMPORTANT]
> This repository is part of a Codex-assisted rewrite experiment. All changes are manually reviewed, a human remains in the loop, and missing behavior is tracked explicitly rather than hidden. The project exists for fun, research, language learning, AI agent workflow/planning, interop experiments, and code review testing.
# java-stakeholder

Java port of `rust-stakeholder`, rebased onto the expanded 2026+ source baseline.

## Status
- Java foundation slices are landed:
  - CLI/config/domain
  - deterministic JSON event output
- The current tranche has landed the registry-backed scheduler/runtime foundation and dedicated family-depth renderers across classic-six, modern-core, ai-governance, security-blockchain, health-protocol, and overlay-quantum.
- The Dockerized Java 25 build now passes with tests and packages a runnable image.
- Java is actively expanding toward full live-provider parity: it already carries a JVM-native provider runtime alongside `javascript-stakeholder`, but the lane remains opt-in and still has explicit hardening gaps.
- JavaScript remains strongest on browser/session/UI flows; Java mirrors the same canonical experimental shapes for profiles, prompt assets, personalization, cache/provenance, consumer-session replay, and opt-in live provider runs.

## Command contract
- `./mvnw -q test`
- `./mvnw -q package`
- `./mvnw -q spotless:check`
- `./mvnw -q checkstyle:check`
- `./mvnw -q spotbugs:check`
- `docker build -t java-stakeholder .`
- `docker run --rm java-stakeholder --list-values`

## Java 25 note
- The repo enforces Java 25 through Maven Enforcer.
- If the host does not provide Java 25, use the Docker path instead of weakening the gate.

## Example usage
```bash
docker run --rm java-stakeholder --list-values
docker run --rm java-stakeholder --dev-type security --jargon high --complexity extreme --alerts --seed 42
docker run --rm java-stakeholder --project "hospital-ocpp-quantum-control" --framework "mcp grpc" --output-format json --seed 11
docker run --rm java-stakeholder --dev-type data-science --project "retrieval-governance-hub" --output-format json --seed 23
docker run --rm -e STAKEHOLDER_ENCRYPTION_KEY=demo-key java-stakeholder --experimental-provider local-demo --output-format json --seed 17
```

## Docs
- [Tooling](docs/tooling.md)
- [Docker](docs/docker.md)
- [Edge cases](docs/edge-cases.md)
- [Language specialties](docs/language-specialties.md)
- [Example outputs](docs/example-outputs.md)
- [Experimental](docs/experimental.md)
- [Traceability](docs/traceability/README.md)

## Contributing
See [CONTRIBUTING.md](CONTRIBUTING.md). Use Conventional Commits and keep Java behavior aligned to Rust + `stakeholder-core`.
