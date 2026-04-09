# Java Language Specialties

- Java uses Picocli, JAnsi, and Maven to mirror the Rust baseline in a Java 25 environment.
- The Java runtime is intentionally structured around explicit scheduler logic and normalized events so parity drift stays visible.
- Docker is the primary local path when the host does not provide Java 25.
