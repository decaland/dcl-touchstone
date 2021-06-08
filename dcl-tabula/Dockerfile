# syntax = docker/dockerfile:1.2.1
# https://hub.docker.com/r/docker/dockerfile/tags

##
## STAGE 1: Builder
##
FROM adoptopenjdk/openjdk11:alpine as builder
WORKDIR build

ARG DCL_APP_NAME=dcl-tabula

COPY gradlew                                gradlew
COPY gradle.properties                      gradle.properties
COPY settings.gradle                        settings.gradle
COPY gradle                                 gradle
COPY ${DCL_APP_NAME}-app/build.gradle       ${DCL_APP_NAME}-app/build.gradle
COPY ${DCL_APP_NAME}-client/build.gradle    ${DCL_APP_NAME}-client/build.gradle
COPY ${DCL_APP_NAME}-model/build.gradle     ${DCL_APP_NAME}-model/build.gradle

RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon downloadDependencies

COPY ${DCL_APP_NAME}-app/src                ${DCL_APP_NAME}-app/src
COPY ${DCL_APP_NAME}-client/src             ${DCL_APP_NAME}-client/src
COPY ${DCL_APP_NAME}-model/src              ${DCL_APP_NAME}-model/src

RUN --mount=type=cache,target=/root/.gradle ./gradlew --no-daemon build

RUN mkdir -p exploded-jar
RUN unzip -q ${DCL_APP_NAME}-app/build/libs/${DCL_APP_NAME}-app.jar -d exploded-jar

##
## STAGE 2: Runner
##
FROM adoptopenjdk/openjdk11:alpine-jre

ARG DCL_USER=dcl
ARG DCL_GROUP=dcl
ARG DCL_JDWP_PORT=5005
ARG DCL_PROD

RUN addgroup -S $DCL_GROUP && adduser -G $DCL_GROUP -S $DCL_USER
USER $DCL_USER:$DCL_GROUP

WORKDIR application

# Java options for PROD
ENV JAVA_OPTS=${DCL_PROD:+""}
# Java options for non-PROD
ENV JAVA_OPTS=${JAVA_OPTS:-"-agentlib:jdwp=transport=dt_socket,address=*:${DCL_JDWP_PORT},server=y,suspend=n"}

COPY --from=builder build/exploded-jar/BOOT-INF/lib     BOOT-INF/lib
COPY --from=builder build/exploded-jar/org              org
COPY --from=builder build/exploded-jar/META-INF         META-INF
COPY --from=builder build/exploded-jar/.                .

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -cp /application org.springframework.boot.loader.JarLauncher"]
