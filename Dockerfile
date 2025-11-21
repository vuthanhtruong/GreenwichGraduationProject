FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src

RUN ./mvnw -q -DskipTests clean package

# ============================
# FINAL IMAGE
# ============================
FROM eclipse-temurin:21-jre
WORKDIR /app

# Your Maven finalName is "app" → target/app.jar
COPY --from=build /app/target/app.jar app.jar

ENV PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]
