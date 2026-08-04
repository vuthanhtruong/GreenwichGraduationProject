FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY common-event/pom.xml common-event/pom.xml
COPY common-event/src common-event/src
RUN mvn -q -f common-event/pom.xml install -DskipTests
COPY email-service/pom.xml email-service/pom.xml
COPY email-service/src email-service/src
RUN mvn -q -f email-service/pom.xml package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/email-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
