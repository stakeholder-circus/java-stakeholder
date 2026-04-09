# Java Edge Cases

- Host JDKs older than 25 are unsupported for the native Maven path; use Docker instead.
- Deterministic JSON output must remain stable under seed even while the runtime expands.
- Feature gaps should fail explicitly and be tracked in `GAPS.md`.
- Experimental provider hooks must not leak into default parity output.
