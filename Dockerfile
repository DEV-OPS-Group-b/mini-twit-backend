FROM openjdk:11
ARG JAR_FILE=target/mini-twit-backend-0.0.1-SNAPSHOT-exec.jar
COPY ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]
