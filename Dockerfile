FROM gradle:7.6-jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle jar --no-daemon
ENV BROKER tcp://localhost:1883
ENV REFBOX localhost
ENTRYPOINT ["sh", "-c", "java -jar /home/gradle/src/app/build/libs/app-all.jar -b $BROKER -r $REFBOX"]