FROM openjdk:11.0.13-jre-slim-bullseye

WORKDIR /app

COPY src/main/resources/application.yml /app/config/
COPY target/mqtt-driver-demo-*.jar /app/mqtt-driver-demo.jar
ENTRYPOINT ["java","-jar","mqtt-driver-demo.jar"]
