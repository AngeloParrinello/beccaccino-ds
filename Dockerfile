# Alpine Linux , latest version
FROM alpine:latest
# Install OpenJDK 11
RUN apk update ; apk add openjdk11
# Create environment variable for container working dir path
ENV WORKINGDIR=/home/gradle/src
# Change working dir
WORKDIR ${WORKINGDIR}
# Create dir (if dosen't exist) and concede full rights
RUN mkdir -p ${WORKINGDIR} && chmod 666 ${WORKINGDIR}
# Copy the Gradle config, source code, and static analysis config
# into the build container. Without --chown gradle throws a permission error
COPY --chown=gradle:gradle . ${WORKINGDIR}
# Install netcat for nc command that is used in script: in this way we can attend for a service
RUN apk update && apk add netcat-openbsd
# Concede rights at the script
RUN chmod +x /home/gradle/src/script.sh
RUN chmod +x gradlew
# Build both client and server build.gradle.
RUN ./gradlew build
# Run the script that run both server and client
CMD ./script.sh
