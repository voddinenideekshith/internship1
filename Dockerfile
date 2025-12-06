# Use Eclipse Temurin OpenJDK 21 as base image
FROM mcr.microsoft.com/openjdk/jdk:21-ubuntu

# Set working directory
WORKDIR /app

# Copy built jar from Maven target directory
COPY target/app.jar app.jar

# Expose port 8080 (matches Azure Container App config)
EXPOSE 8080

# Run Spring Boot app on port 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
