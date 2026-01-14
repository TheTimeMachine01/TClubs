FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
# Copy the whole services folder so child module directories referenced in services/pom.xml exist
COPY services ./services
COPY common ./common
COPY services/eureka-server/pom.xml ./services/eureka-server/
RUN mvn -B -DskipTests dependency:go-offline -pl services/eureka-server -am

COPY services/eureka-server/src ./services/eureka-server/src
RUN mvn -B -DskipTests clean package -pl services/eureka-server -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/services/eureka-server/target/*-SNAPSHOT.jar app.jar
# Escape $ so BuildKit doesn't try to substitute at build time
ENV JAVA_OPTS="-Xms128m -Xmx512m -Dspring.profiles.active=\${SPRING_PROFILES_ACTIVE:-default}"
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]