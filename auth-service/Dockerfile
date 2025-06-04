# === Build stage ===
FROM maven:3.9.6-eclipse-temurin-21 AS build

WORKDIR /app

COPY . .

RUN ./mvnw dependency:go-offline

RUN ./mvnw -pl auth-service -am clean package -DskipTests

# === Run stage ===
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/auth-service/target/*.jar app.jar

CMD ["java", "-jar", "app.jar"]