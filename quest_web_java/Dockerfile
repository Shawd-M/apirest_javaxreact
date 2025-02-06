FROM maven:3.8.7-eclipse-temurin-17 AS builder

WORKDIR /app

COPY . .

RUN mvn clean install -DskipTests

FROM openjdk:17-jdk-slim

WORKDIR /app

COPY --from=builder /app/target/quest_web-0.0.1-SNAPSHOT.jar /app/quest_web-0.0.1-SNAPSHOT.jar

EXPOSE 8090

ENTRYPOINT ["java", "-jar", "/app/quest_web-0.0.1-SNAPSHOT.jar"]
