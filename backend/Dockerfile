# --- Build: compile the jar (dependencies are cached in their own layer)
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src src
RUN mvn -q -B package -DskipTests

# --- Run: small JRE image, non-root user
FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --uid 1001 app && mkdir -p uploads && chown app uploads
COPY --from=build /build/target/VoixDeSagesse-0.0.1-SNAPSHOT.jar app.jar
USER app
# Size the heap from the container memory limit instead of the host memory
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75"
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
