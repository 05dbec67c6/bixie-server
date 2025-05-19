# Use a base image with Java (ensure it's compatible with your Ktor/Kotlin setup)
    # Eclipse Temurin is a good choice for OpenJDK. Choose a version that matches your needs.
    # For example, if your project uses Java 17:
    FROM eclipse-temurin:21-jdk-jammy

    # Set a working directory inside the container
    WORKDIR /app

    # Copy the built JAR file into the container
    # The JAR file is typically found in build/libs/ after a Gradle build.
    # Adjust the JAR file name if necessary. It usually follows the pattern: projectname-all.jar or projectname.jar
    # If you use the application or shadow plugin, it's often projectname-all.jar
    COPY build/libs/*.jar app.jar

    # Expose the port your Ktor server listens on (default is 8080)
    EXPOSE 8080

    # Command to run your Ktor application when the container starts
    ENTRYPOINT ["java", "-jar", "app.jar"]