FROM docker.io/library/gradle:8.7-jdk21-alpine AS build
WORKDIR /home/gradle/src
COPY . .

RUN apk add --no-cache dos2unix && \
    dos2unix gradlew && \
    chmod +x gradlew

ENV GRADLE_OPTS="-Dorg.gradle.jvmargs='-Xmx4096m -XX:MaxMetaspaceSize=512m' -Dorg.gradle.daemon=false"
ENV KOTLIN_COMPILER_EXECUTION_STRATEGY="in-process"

RUN --mount=type=cache,target=/home/gradle/.gradle \
    ./gradlew :server:bootJar --no-daemon -Porg.gradle.java.installations.auto-download=false

FROM docker.io/library/eclipse-temurin:21-jre-alpine
EXPOSE 8080
WORKDIR /app
COPY --from=build /home/gradle/src/server/build/libs/*.jar /app/spring-server.jar
ENTRYPOINT ["java", "-jar", "/app/spring-server.jar"]