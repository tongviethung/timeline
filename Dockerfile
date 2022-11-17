FROM adoptopenjdk/openjdk11:alpine-jre
# Refer to Maven build -> finalName
ARG JAR_FILE=build/timeline-service-*.jar

# cd /opt/app
WORKDIR /opt/app

RUN mkdir -p /opt/app/logs/
# cp target/spring-boot-web.jar /opt/app/app.jar
COPY ${JAR_FILE} app.jar

# java -jar /opt/app/app.jar
ENTRYPOINT java $JAVA_OPTS -jar app.jar
