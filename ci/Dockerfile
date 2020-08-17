FROM openjdk:8-jre-alpine

VOLUME /tmp

ADD /build/libs/adrift.jar game.jar

RUN sh -c 'touch /game.jar'

ENV JAVA_OPTS=""
ENV GAME_OPS=""

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /game.jar server $GAME_OPS" ]
