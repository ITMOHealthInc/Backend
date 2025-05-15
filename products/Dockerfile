# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the build files to the container
COPY products/build.gradle.kts .
COPY products/settings.gradle.kts .
COPY products/gradle.properties .
COPY products/gradlew .
COPY products/gradlew.bat .
COPY products/gradle ./gradle

# Copy the source code to the container
COPY products/src ./src

# Build the application with Gradle
RUN ./gradlew build --no-daemon

FROM openjdk:17-jdk-slim
# Copy the built application from the build stage
COPY --from=build /app/build/libs/products-all.jar ./ktor-app.jar

# Expose the port that the application will run on
EXPOSE 5022

# Command to run the application
CMD ["java", "-jar", "ktor-app.jar"]
