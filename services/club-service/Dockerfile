# Multi-stage build using Maven and Java runtime
ENTRYPOINT ["java", "-jar", "app.jar"]
EXPOSE 8083

COPY --from=builder /build/services/club-service/target/club-service-*.jar app.jar

WORKDIR /app
FROM eclipse-temurin:21-jre-alpine
# Runtime stage

RUN mvn -DskipTests clean package -pl common,services/club-service -am

COPY services/club-service ./services/club-service
COPY services/pom.xml ./services/
COPY common ./common
COPY pom.xml ./
# Copy parent pom and common modules

WORKDIR /build
FROM maven:3.9-eclipse-temurin-21-alpine AS builder

