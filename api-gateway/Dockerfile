FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn package -pl api-gateway -am -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/api-gateway/target/api-gateway-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
