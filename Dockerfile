# ------------ Stage 1: Build ------------
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy maven wrapper + pom trước để tận dụng cache
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Copy source
COPY src ./src

# Build (skip test cho nhanh)
RUN ./mvnw -q -DskipTests clean package

# ------------ Stage 2: Runtime ------------
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy JAR đã build (dùng tên cố định cho dễ)
COPY --from=build /app/target/*-SNAPSHOT.jar app.jar
# Nếu bạn đã thêm <finalName>app</finalName> trong pom.xml như mình hướng dẫn trước thì dùng dòng này tốt hơn:
# COPY --from=build /app/target/app.jar app.jar

# Quan trọng nhất: Đọc PORT từ environment và truyền vào Spring Boot
ENV PORT=8080
EXPOSE ${PORT}

# Cách chạy đúng chuẩn cho Render, Heroku, Fly.io, Railway...
ENTRYPOINT exec java -jar /app/app.jar --server.port=${PORT:-8080}