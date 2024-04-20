FROM gradle:7.6-jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon
ENV BROKER tcp://localhost:1883
ENV REFBOX localhost
ENV TEAM GRIPS
ENV KEY randomkey
ENTRYPOINT ["sh", "-c", "java -jar /home/gradle/src/app/build/libs/mqtt-bridge-0.3-all.jar -b $BROKER -r $REFBOX -t $TEAM -k $KEY"]
