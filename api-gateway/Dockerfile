# === Build stage ===
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN ./mvnw dependency:go-offline

RUN ./mvnw -pl api-gateway -am clean package -DskipTests

# === Run stage ===
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/api-gateway/target/*.jar app.jar

CMD ["java", "-Dlogging.level.org.springframework.cloud.gateway=DEBUG", "-jar", "app.jar"]