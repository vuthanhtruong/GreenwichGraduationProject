FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src

RUN chmod +x mvnw
RUN ./mvnw -q -DskipTests clean package

FROM eclipse-temurin:21-jdk
WORKDIR /app

COPY --from=build /app/target/app.jar app.jar

ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
