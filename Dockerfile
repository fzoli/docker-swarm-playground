# Build stage
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /app
COPY . .
RUN chmod +x gradlew
RUN ./gradlew bootJar -x test

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the fat jar produced by bootJar
COPY --from=build /app/build/libs/demo-0.1.0.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]