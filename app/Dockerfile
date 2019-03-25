FROM openjdk:8u191-jdk-alpine3.9
LABEL MAINTAINER='Maksim Kostromin https://github.com/daggerok'
EXPOSE 8080
CMD /bin/ash
WORKDIR /tmp/app
ENTRYPOINT java -jar *.war
HEALTHCHECK CMD wget -q --spider http://127.0.0.1:8080/actuator/health || exit 1
COPY build/libs/*.war /tmp/app/
