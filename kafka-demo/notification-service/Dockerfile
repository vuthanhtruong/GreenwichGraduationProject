FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY common-event/pom.xml common-event/pom.xml
COPY common-event/src common-event/src
RUN mvn -q -f common-event/pom.xml install -DskipTests
COPY notification-service/pom.xml notification-service/pom.xml
COPY notification-service/src notification-service/src
RUN mvn -q -f notification-service/pom.xml package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/notification-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
