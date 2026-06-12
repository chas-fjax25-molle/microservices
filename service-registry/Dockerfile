FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn package -pl service-registry -am -DskipTests

FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY --from=build /app/service-registry/target/service-registry-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
