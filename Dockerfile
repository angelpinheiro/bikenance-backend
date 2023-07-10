# BUILD STAGE ---
FROM openjdk:11 AS build
RUN mkdir /appbuild
COPY . /appbuild
WORKDIR /appbuild
RUN ./gradlew clean build --no-daemon
# END BUILD STAGE ---

# BUILD BIKENANCE IMAGE ---
FROM openjdk:11
EXPOSE 8080:8080

RUN mkdir /app

# Copy Bikenance server executable from the build image
COPY --from=build  /appbuild/build/libs/*-all.jar /app/bikenance-backend.jar

ENTRYPOINT ["java","-jar","/app/bikenance-backend.jar"]


