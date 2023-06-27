FROM openjdk:11 AS build
RUN mkdir /appbuild
COPY . /appbuild
WORKDIR /appbuild
RUN ./gradlew clean build


FROM openjdk:11
EXPOSE 8080:8080

RUN mkdir /app
RUN mkdir /app/uploads

# Copy Bikenance server executable
COPY --from=build  /appbuild/build/libs/*-all.jar /app/bikenance-backend.jar

ENTRYPOINT ["java","-jar","/app/bikenance-backend.jar"]


