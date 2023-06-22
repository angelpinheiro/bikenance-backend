FROM openjdk:11
EXPOSE 8080:8080

RUN mkdir /app

ENV FIREBASE_ADMIN_JSON /app/resources/firebase.json

# Copy resources (firebase config, log config,...)
COPY ./build/resources/main /app/resources/
# Copy Bikenance server executable
COPY ./build/libs/*-all.jar /app/bikenance-backend.jar

ENTRYPOINT ["java","-jar","/app/bikenance-backend.jar"]