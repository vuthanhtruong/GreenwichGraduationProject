# ===== Stage 1: Build jar =====
FROM maven:3-eclipse-temurin-17 AS build
WORKDIR /app

# Copy file Maven config trước để cache dependency
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copy source code
COPY src ./src

# Build jar
RUN mvn -q -DskipTests clean package

# ===== Stage 2: Run jar =====
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy jar từ stage build
COPY --from=build /app/target/*.jar app.jar

# Render sẽ set PORT, mặc định của nó là 10000 nếu bạn không override
# Không bắt buộc, nhưng EXPOSE cho rõ ý đồ (Render không phụ thuộc EXPOSE) :contentReference[oaicite:5]{index=5}
EXPOSE 8080

# Start command: dùng PORT do Render cấp
CMD ["sh", "-c", "java -jar app.jar --server.port=${PORT}"]
