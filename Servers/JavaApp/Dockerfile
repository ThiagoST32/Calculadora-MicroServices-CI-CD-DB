FROM eclipse-temurin:21-jdk-alpine
LABEL authors="thiago-sandre-trevisan"

WORKDIR /app

COPY target/CalculadoraMicroServicesDB-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]