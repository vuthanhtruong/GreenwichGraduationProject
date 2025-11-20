# ------------ Stage 1: Build ------------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy toàn bộ project
COPY . .

# Build Maven (bỏ test cho nhanh)
RUN ./mvnw -q -DskipTests package


# ------------ Stage 2: Run ------------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy file JAR từ stage build
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
