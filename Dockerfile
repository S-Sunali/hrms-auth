FROM adoptopenjdk/openjdk11:jre-11.0.12_7-alpine
ENV TZ="Asia/Kolkata"
ARG JAR_FILE=./target/authservice-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /usr/app/authservice.jar
WORKDIR /usr/app
ENTRYPOINT ["java","-jar","authservice.jar"]