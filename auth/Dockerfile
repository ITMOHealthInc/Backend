# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the build files to the container
COPY auth/build.gradle.kts .
COPY auth/settings.gradle.kts .
COPY auth/gradle.properties .
COPY auth/gradlew .
COPY auth/gradlew.bat .
COPY auth/gradle ./gradle

# Copy the source code to the container
COPY auth/src ./src

# Build the application with Gradle
RUN ./gradlew build --no-daemon

# Use a smaller base image for the final stage
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built application from the build stage
COPY --from=build /app/build/libs/auth-all.jar ./ktor-app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "ktor-app.jar"]
