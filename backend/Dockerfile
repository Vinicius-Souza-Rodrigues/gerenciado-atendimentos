# Build stage
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app/backend

# Copiar arquivos de configuração do root
COPY checkstyle.xml ../
COPY .spotbugsExcludeFilter.xml ../

# Copiar pom.xml e source do backend
COPY backend/pom.xml .
RUN mvn dependency:resolve

COPY backend/src src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/backend/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
