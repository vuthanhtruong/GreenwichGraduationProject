# ========= BUILD + RUN trong 1 Dockerfile =========
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy maven wrapper trước để cache
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
COPY src ./src
RUN chmod +x mvnw

# Build JAR (tương đương ./mvnw clean package -DskipTests)
RUN ./mvnw clean package -DskipTests -q

# ========= RUNTIME =========
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Force bind đúng port mà Render tự set (10000)
ENV JAVA_TOOL_OPTIONS="-Dserver.address=0.0.0.0 -Dserver.port=${PORT}"
EXPOSE ${PORT}

ENTRYPOINT ["java", "-jar", "app.jar"]