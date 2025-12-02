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

# CHỈ copy đúng file app.jar
COPY --from=build /app/target/app.jar app.jar

# ÉP port theo biến PORT (Render set sẵn)
ENTRYPOINT ["sh", "-c", "java -jar app.jar --server.port=${PORT:-8080}"]
