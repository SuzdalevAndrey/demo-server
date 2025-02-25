FROM maven:3.8.5-openjdk-17-slim AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src /app/src

RUN mvn clean package -DskipTests

FROM openjdk:17-jdk-slim

COPY --from=builder /app/target/demo-server-1.0-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]

EXPOSE 8082