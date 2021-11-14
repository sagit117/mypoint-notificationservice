ARG VERSION=17

FROM openjdk:${VERSION}-jdk as BUILD

COPY . /src
WORKDIR /src
RUN ./gradlew --no-daemon shadowJar

FROM openjdk:${VERSION}-jdk

COPY --from=BUILD /src/build/libs/notificationService-0.0.1-all.jar /bin/notificationservice/app.jar
COPY ./resources/prod.conf /bin/notificationservice/
WORKDIR /bin/notificationservice

EXPOSE 8082:8082

CMD ["java","-jar","app.jar", "-config=prod.conf"]