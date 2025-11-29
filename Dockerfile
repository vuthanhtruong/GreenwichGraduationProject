# Stage 1: Build
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy các file cấu hình Maven
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .

# Copy mã nguồn
COPY src ./src

# Cấp quyền thực thi cho mvnw
RUN chmod +x mvnw

# Build project, bỏ qua test
RUN ./mvnw -q -DskipTests clean package


# Stage 2: Runtime
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy file jar đã build từ stage 1 sang
COPY --from=build /app/target/app.jar app.jar

# Lệnh chạy ứng dụng
ENTRYPOINT ["sh", "-c", "java -jar app.jar"]
