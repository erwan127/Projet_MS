FROM openjdk:17-jdk-slim

# Create app directory
WORKDIR /app

# Copy Maven wrapper files
COPY mvnw .
COPY .mvn .mvn

# Copy pom.xml
COPY pom.xml .

# Copy source code
COPY src src

# Build the application
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests

# Copy the built JAR file
RUN cp target/*.jar app.jar

# Expose the port the app runs on
EXPOSE 8084

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"] 