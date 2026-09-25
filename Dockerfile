# Stage 1: compile the Spring Boot application inside a reproducible Java 21 environment.
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /workspace

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# Copy source after Gradle metadata so Docker can reuse downloaded dependencies when source changes.
COPY src src
RUN ./gradlew bootJar --no-daemon

# Stage 2: the runtime image contains only a JRE and the executable application jar.
FROM eclipse-temurin:21-jre

WORKDIR /app
COPY --from=builder /workspace/build/libs/OnlineOrder-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
