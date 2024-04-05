FROM ghcr.io/twopair/lotto-gradle-cache:latest AS build
COPY . /app
WORKDIR /app

RUN ./gradlew assemble

FROM amazoncorretto:21-alpine-jdk AS cache

ENV TZ=Asia/Seoul

## spring 패키지 복사
COPY --from=build \
  /app/build/libs/*.jar \
  /app/app.jar

EXPOSE 8080

ENTRYPOINT java \
           -Dspring.profiles.active=prod \
           -jar /app/app.jar
