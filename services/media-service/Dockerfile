# Multi-stage build using Maven and Java runtime
FROM maven:3.9-eclipse-temurin-21-alpine AS builder
WORKDIR /build

# Copy parent pom and common modules
COPY pom.xml ./
COPY common ./common
COPY services/pom.xml ./services/
COPY services/media-service ./services/media-service

RUN mvn -DskipTests clean package -pl common,services/media-service -am

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY --from=builder /build/services/media-service/target/media-service-*.jar app.jar

EXPOSE 8085
ENTRYPOINT ["java", "-jar", "app.jar"]

