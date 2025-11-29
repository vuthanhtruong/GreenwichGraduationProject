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

# Copy bất kỳ file jar nào build ra và đặt tên lại thành app.jar
COPY --from=build /app/target/*.jar app.jar

# Cho platform biết container listen port 8080
EXPOSE 8080

# Chạy thẳng java -jar cho sạch
ENTRYPOINT ["java", "-jar", "app.jar"]
