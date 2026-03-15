# Build stage
FROM maven:3.8.5-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Run stage
# Use eclipse-temurin instead of the deprecated openjdk image
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 1010
ENTRYPOINT ["java","-jar","app.jar"]