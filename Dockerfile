# ============ BUILD STAGE ============
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy maven wrapper + pom trước để tận dụng cache
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src

# Chmod và build
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests -q

# ============ RUNTIME STAGE ============
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copy đúng file JAR có tên bất kỳ (demo-0.0.1-SNAPSHOT.jar, app.jar, ...)
COPY --from=build /app/target/*.jar app.jar

# Quan trọng nhất cho Render: dùng $PORT do Render tự inject
CMD java -jar app.jar --server.port=$PORT

# Render yêu cầu EXPOSE (dù không bắt buộc nhưng tốt nên có)
EXPOSE $PORT