FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /build
COPY pom.xml .
RUN mvn -q -B dependency:go-offline
COPY src src
RUN mvn -q -B package -DskipTests \
    && java -Djarmode=tools -jar target/VoixDeSagesse-0.0.1-SNAPSHOT.jar extract --destination app

FROM eclipse-temurin:17-jre
WORKDIR /app
RUN useradd --system --uid 1001 app && mkdir -p uploads && chown app uploads
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75 -XX:TieredStopAtLevel=1 -XX:+UseSerialGC"
COPY --from=build /build/app ./
RUN JWT_SECRET=$(head -c 48 /dev/urandom | base64) java -XX:ArchiveClassesAtExit=app.jsa \
    -Dspring.context.exit=onRefresh -Dspring.data.mongodb.auto-index-creation=false -jar VoixDeSagesse-0.0.1-SNAPSHOT.jar
USER app
EXPOSE 8080
ENTRYPOINT ["java", "-XX:SharedArchiveFile=app.jsa", "-jar", "VoixDeSagesse-0.0.1-SNAPSHOT.jar"]
