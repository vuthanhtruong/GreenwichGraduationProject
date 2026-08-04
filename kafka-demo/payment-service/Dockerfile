# Build context là thư mục gốc kafka-demo (xem docker-compose.yml)
# Stage 1: build common-event roi build service
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY common-event/pom.xml common-event/pom.xml
COPY common-event/src common-event/src
RUN mvn -q -f common-event/pom.xml install -DskipTests
COPY payment-service/pom.xml payment-service/pom.xml
COPY payment-service/src payment-service/src
RUN mvn -q -f payment-service/pom.xml package -DskipTests

# Stage 2: runtime image gon nhe, chi can JRE
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/payment-service/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
