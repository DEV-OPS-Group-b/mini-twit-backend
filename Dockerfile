FROM maven:3.6.3-jdk-11 AS builder
ENV APP_HOME=/app/
WORKDIR $APP_HOME
COPY pom.xml $APP_HOME
RUN mvn -B dependency:go-offline

COPY . .
RUN mvn package -DskipTests

FROM openjdk:11-jre-slim
ARG JAR_FILE=/app/target/mini-twit-backend-0.0.1-SNAPSHOT-exec.jar
COPY --from=builder ${JAR_FILE} app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app.jar"]


