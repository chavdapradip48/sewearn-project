# ---- Build stage
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /sewearn

# Copy pom first to cache dependencies
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copy source files and build the JAR
COPY src ./src
RUN mvn clean package -DskipTests

# ---- Runtime stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /sewearn

# Copy the built jar file from the build stage
COPY --from=build /sewearn/target/*.jar sewearn.jar

# Expose the Spring Boot default port
EXPOSE 8080

# Allow optional JVM options
ENV JAVA_OPTS=""

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar sewearn.jar"]
