# Stage 1: Build
FROM maven:3.9.5-eclipse-temurin-17 AS build
WORKDIR /app

# Copiar archivos de configuración de Maven
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar código fuente y compilar
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Copiar el JAR compilado desde la etapa de build
COPY --from=build /app/target/*.jar app.jar

# Exponer el puerto de la aplicación
EXPOSE 8081

# Variables de entorno por defecto
ENV MONGO_HOST=mongodb
ENV MONGO_PORT=27017
ENV MONGO_DB=franchisedb

# Ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "app.jar"]