# cache step
FROM gradle:6.5.0-jdk8 as cache

# cache dir
RUN mkdir -p /cache
ENV GRADLE_USER_HOME /cache

# change working dir to an empty dir to prevent gradle build from ziping everything in /
WORKDIR /app

# copy only build.gradle, this file is cached if not changed
COPY build.gradle .
RUN gradle build --no-daemon --info --stacktrace



# build step
FROM gradle:6.5.0-jdk8 as builder
WORKDIR /app

# cache data
COPY --from=cache /cache /home/gradle/.gradle

# source code
COPY . .

RUN gradle build --no-daemon --info --stacktrace

# move /app/build/distributions/*.tar -> /app/build/distributions/app, main script: bin/??? -> bin/main
RUN cd /app/build/distributions && \
    tar xf *.tar && \
    mv */ app && \
    cd app/bin && \
    find -type f -not -name '*.bat' -exec mv {} main \;



# final image
FROM openjdk:8-jre-slim
RUN groupadd -g 20000 ckanapp && useradd --create-home -u 20000 -g ckanapp ckanapp
COPY --from=builder --chown=ckanapp:ckanapp /app/build/distributions/app /app

RUN mkdir /inputs /outputs && chown -R ckanapp:ckanapp /inputs /outputs

USER ckanapp
WORKDIR /app

ENV TIMEOUT 60
ENV JAVA_OPTS "-XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -XX:MaxRAMFraction=2"

CMD ["sh", "-c", "/usr/bin/timeout --kill-after=5 $TIMEOUT /app/bin/main"]

