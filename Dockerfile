###############################################################################

FROM maven:3.9.6-sapmachine-17 as build
COPY . /build

RUN cd /build && \
    mvn clean install -Dmaven.test.skip=true -Dfile.encoding=UTF-8 -B

###############################################################################

FROM openjdk:17-jdk as watering-flowers-bot

RUN useradd afrize && \
	mkdir /opt/log && \
	chown -R afrize /opt

COPY --from=build /build/watering-flowers-bot-application/target/watering-flowers-bot-application.jar /opt/watering-flowers-bot.jar
COPY --from=build /build/watering-flowers-bot-application/target/lib/logback.xml /opt/logback.xml

USER afrize
WORKDIR /opt
EXPOSE 8080

ENTRYPOINT exec java -Duser.timezone=Europe/Moscow $JAVA_OPTS -jar /opt/watering-flowers-bot.jar --logging.config=/opt/logback.xml --server.port=8080