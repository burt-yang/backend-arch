FROM openjdk:8-jre

ENV JAVA_OPTS=""
ENV APP_HOME /var/app
ENV LOG ${APP_HOME}/logs
ENV DATA_PATH_FILES ${APP_HOME}/dataFiles
ENV RESOURCES ${APP_HOME}/resources

RUN mkdir -p "$APP_HOME" && \
    mkdir -p "${LOG}" && \
    mkdir -p "${DATA_PATH_FILES}" && \
    mkdir -p "${RESOURCES}"

VOLUME ["/tmp","${APP_HOME}","${LOG}","${DATA_PATH_FILES}","${RESOURCES}"]

COPY *.jar app.jar

ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -jar /app.jar" ]
