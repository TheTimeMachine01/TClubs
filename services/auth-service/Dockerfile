FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
# Copy the whole services folder so child module directories referenced in services/pom.xml exist
COPY services ./services
COPY common ./common
COPY services/auth-service/pom.xml ./services/auth-service/
RUN mvn -B -DskipTests dependency:go-offline -pl services/auth-service -am

COPY services/auth-service/src ./services/auth-service/src
RUN mvn -B -DskipTests clean package -pl services/auth-service -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/services/auth-service/target/*-SNAPSHOT.jar app.jar
ENV JAVA_OPTS="-Xms128m -Xmx512m -Dspring.profiles.active=\${SPRING_PROFILES_ACTIVE:-default}"
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]