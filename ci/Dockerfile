FROM openjdk:8-jre-alpine

VOLUME /tmp

ADD /build/libs/*.jar game.jar

RUN sh -c 'touch /game.jar'

ENV JAVA_OPTS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /game.jar server" ]
