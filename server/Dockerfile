FROM docker.io/library/gradle:8.7-jdk21-alpine AS build
WORKDIR /home/gradle/src
COPY --chown=gradle:gradle . .
RUN chmod +x gradlew

ENV GRADLE_OPTS="-Dorg.gradle.jvmargs='-Xmx1024m -XX:MaxMetaspaceSize=512m' -Dorg.gradle.daemon=false"
ENV KOTLIN_COMPILER_EXECUTION_STRATEGY="in-process"

RUN ./gradlew :server:buildFatJar --no-daemon -Porg.gradle.java.installations.auto-download=false

FROM docker.io/library/eclipse-temurin:21-jre-alpine
EXPOSE 8080
WORKDIR /app
COPY --from=build /home/gradle/src/server/build/libs/*.jar /app/ktor-server.jar
ENTRYPOINT ["java", "-jar", "/app/ktor-server.jar"]