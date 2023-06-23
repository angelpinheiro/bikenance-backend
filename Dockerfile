#TODO: Use a previous step for buildind the project

FROM openjdk:11
EXPOSE 8080:8080

RUN mkdir /app
RUN mkdir /app/uploads

# Copy Bikenance server executable
COPY ./build/libs/*-all.jar /app/bikenance-backend.jar

ENTRYPOINT ["java","-jar","/app/bikenance-backend.jar"]