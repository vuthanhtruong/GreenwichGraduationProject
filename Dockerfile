# ===== Stage 1: Build JAR =====
FROM maven:3-eclipse-temurin-17 AS build
WORKDIR /app

# Copy Maven config để cache dependency
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copy source code
COPY src ./src

# Build jar
RUN mvn -q -DskipTests clean package

# ===== Stage 2: Run JAR =====
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copy jar (dùng wildcard vẫn ok)
COPY --from=build /app/target/*.jar app.jar

# Expose port (optional)
EXPOSE 8080

# Start using $PORT from Render
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
