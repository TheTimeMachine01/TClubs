FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
# Copy the whole services folder so child module directories referenced in services/pom.xml exist
COPY services ./services
COPY common ./common
COPY services/user-service/pom.xml ./services/user-service/
RUN mvn -B -DskipTests dependency:go-offline -pl services/user-service -am

COPY services/user-service/src ./services/user-service/src
RUN mvn -B -DskipTests clean package -pl services/user-service -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/services/user-service/target/*-SNAPSHOT.jar app.jar
# Escape $ so BuildKit doesn't try to substitute at build time
ENV JAVA_OPTS="-Xms128m -Xmx512m -Dspring.profiles.active=\${SPRING_PROFILES_ACTIVE:-default}"
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]