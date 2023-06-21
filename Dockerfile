FROM openjdk:11
EXPOSE 8080:8080

ENV DB_HOST localhost
ENV FIREBASE_ADMIN_JSON /app/firebase.json

RUN mkdir /app
# firebase config file
COPY ./src/main/resources/firebase.json /app/firebase.json
COPY ./build/libs/*-all.jar /app/bikenance-backend.jar

ENTRYPOINT ["java","-jar","/app/bikenance-backend.jar"]