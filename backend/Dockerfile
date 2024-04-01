# Use an official Java runtime image as the base
FROM openjdk:17-jdk-slim

# Expose the application's port to the container
EXPOSE 8080

# Copy the .war file from the target directory (relative to the Docker build context) into the container's application root
COPY target/baccaratio-1.0.0.jar /app.jar

# Execute the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
