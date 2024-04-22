ARG JAVA_SDK_USERNAME
ARG GHP_JAVA_SDK_TOKEN
ENV GHP_JAVA_SDK_TOKEN $GHP_JAVA_SDK_TOKEN
ENV JAVA_SDK_USERNAME $JAVA_SDK_USERNAME
FROM gradle:7.6-jdk11-alpine AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle build --no-daemon
ENV BROKER tcp://localhost:1883
ENV REFBOX localhost
ENV TEAM GRIPS
ENV KEY randomkey
ENTRYPOINT ["sh", "-c", "java -jar /home/gradle/src/app/build/libs/mqtt-bridge-0.4-all.jar -b $BROKER -r $REFBOX -t $TEAM -k $KEY"]
