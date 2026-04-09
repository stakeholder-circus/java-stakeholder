FROM eclipse-temurin:25-jdk AS build
RUN apt-get update \
    && apt-get install -y --no-install-recommends maven \
    && rm -rf /var/lib/apt/lists/*
ENV MAVEN_OPTS="--enable-native-access=ALL-UNNAMED"
WORKDIR /workspace
COPY . .
RUN chmod +x mvnw && ./mvnw -q spotless:check checkstyle:check spotbugs:check test package

FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /workspace/target/java-stakeholder.jar /app/java-stakeholder.jar
ENTRYPOINT ["java", "--enable-native-access=ALL-UNNAMED", "-jar", "/app/java-stakeholder.jar"]
CMD ["--list-values"]
