FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /workspace
COPY pom.xml .
COPY common ./common
COPY services/dummy-service/pom.xml ./services/dummy-service/
RUN mvn -B -DskipTests dependency:go-offline -pl services/dummy-service -am

COPY services/dummy-service/src ./services/dummy-service/src
RUN mvn -B -DskipTests clean package -pl services/dummy-service -am

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/services/dummy-service/target/*-SNAPSHOT.jar app.jar
ENV JAVA_OPTS="-Xms128m -Xmx512m -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-default}"
EXPOSE 8080
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]