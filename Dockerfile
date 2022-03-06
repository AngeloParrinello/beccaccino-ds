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
# Best solution we've found so far is to install glibc into our alpine image (protobuf-plugin-gradle, that contains protoc,
# artifact is compiled for glibc). Instructions on how to do this can be found here: https://github.com/sgerrand/alpine-pkg-glibc
RUN apk update
RUN wget -q -O /etc/apk/keys/sgerrand.rsa.pub https://alpine-pkgs.sgerrand.com/sgerrand.rsa.pub
RUN wget https://github.com/sgerrand/alpine-pkg-glibc/releases/download/2.34-r0/glibc-2.34-r0.apk
RUN apk add glibc-2.34-r0.apk
# Concede rights at the script
RUN chmod +x /home/gradle/src/script.sh
RUN chmod +x /home/gradle/src/gradlew
# Build both client and server build.gradle.
# RUN ./gradlew build -x test --stacktrace --info
# Run the script that run both server and client
CMD ./script.sh
