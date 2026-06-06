FROM eclipse-temurin:21-jdk

RUN apt-get update && \
    apt-get install -y git && \
    apt-get clean

WORKDIR /app

COPY build/libs/*.jar app.jar

EXPOSE 8088

ENTRYPOINT ["java","-jar","app.jar"]