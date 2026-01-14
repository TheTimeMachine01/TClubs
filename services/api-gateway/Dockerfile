FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
# Copy the whole services folder so child module directories referenced in services/pom.xml exist
COPY services ./services
COPY common ./common
COPY services/api-gateway/pom.xml ./services/api-gateway/
RUN mvn -B -DskipTests dependency:go-offline -pl services/api-gateway -am

COPY services/api-gateway/src ./services/api-gateway/src
RUN mvn -B -DskipTests clean package -pl services/api-gateway -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/services/api-gateway/target/*-SNAPSHOT.jar app.jar
ENV JAVA_OPTS="-Xms128m -Xmx512m -Dspring.profiles.active=\${SPRING_PROFILES_ACTIVE:-default}"
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]