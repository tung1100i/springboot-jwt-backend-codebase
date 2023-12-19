FROM openjdk:11
WORKDIR /app
COPY target/tech-shop-0.0.1-SNAPSHOT.jar vrp-app.jar
CMD ["java", "-jar", "vrp-app.jar"]