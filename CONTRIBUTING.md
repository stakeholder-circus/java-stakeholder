# Contributing to java-stakeholder

## Rules
- Java tracks the Rust source baseline and `stakeholder-core` contract.
- Use Conventional Commits.
- Do not paper over missing parity; keep feature-level gaps explicit.
- Keep deterministic JSON behavior stable when a seed is supplied.

## Local workflow
- `./mvnw -q spotless:check`
- `./mvnw -q checkstyle:check`
- `./mvnw -q spotbugs:check`
- `./mvnw -q test package`
- `docker build -t java-stakeholder .`
- `docker run --rm java-stakeholder --list-values`

## Change discipline
- Runtime/scheduler changes should be reconciled against Rust and `stakeholder-core` in the same tranche.
- Experimental provider hooks must remain outside deterministic parity CI.
- Prefer explicit gap ids over TODO comments or silent fallbacks.
