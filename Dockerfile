ARG VERSION=8u151

FROM openjdk:${VERSION}-jdk as BUILD

COPY . /src
WORKDIR /src
RUN ./gradlew --no-daemon shadowJar

FROM openjdk:${VERSION}-jre

COPY --from=BUILD /src/build/libs/notificationService-0.0.1-all.jar /bin/notificationservice/app.jar
COPY ./resources/application.conf /bin/notificationservice/
WORKDIR /bin/notificationservice

EXPOSE 8082:8080

CMD ["java","-jar","app.jar", "-config=application.conf"]