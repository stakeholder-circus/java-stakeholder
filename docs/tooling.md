# Java Tooling

## Commands
- `./mvnw -q test`
- `./mvnw -q package`
- `./mvnw -q spotless:check`
- `./mvnw -q checkstyle:check`
- `./mvnw -q spotbugs:check`

## Notes
- The repo enforces Java 25 through Maven Enforcer.
- Host builds on older JDKs should use the Docker path instead of weakening the gate.
- The Docker path passes `--enable-native-access=ALL-UNNAMED` through Maven and the packaged JVM command so Java 25 runs Jansi without noisy native-access warnings.
- `docker build -t java-stakeholder .` now runs `spotless:check`, `checkstyle:check`, `spotbugs:check`, `test`, and `package` inside the image build.
