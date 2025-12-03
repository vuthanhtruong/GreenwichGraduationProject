# BUILD
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests -q

# RUNTIME
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

ENV JAVA_TOOL_OPTIONS="-Dserver.address=0.0.0.0 -Dserver.port=${PORT}"

# Expose port động để Render detect
EXPOSE ${PORT}

# Chạy app
ENTRYPOINT ["java", "-jar", "app.jar"]