FROM maven:latest AS builder

ENV APP_HOME=/app/
COPY pom.xml $APP_HOME
COPY src $APP_HOME/src/
WORKDIR $APP_HOME
RUN mvn package -DskipTests

FROM openjdk:11
ARG JAR_FILE=/app/target/mini-twit-backend-0.0.1-SNAPSHOT-exec.jar
COPY --from=builder ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]


