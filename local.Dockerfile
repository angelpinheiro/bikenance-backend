# BUILD BIKENANCE IMAGE FROM COMPILED JAR  ---
FROM openjdk:11
EXPOSE 8080:8080

RUN mkdir /app

# Copy Bikenance server executable from the build image
COPY ./build/libs/*-all.jar /app/bikenance-backend.jar

ENTRYPOINT ["java","-jar","/app/bikenance-backend.jar"]


