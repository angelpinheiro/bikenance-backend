FROM openjdk:11
EXPOSE 8080:8080

RUN mkdir /app

# Copy Bikenance server executable
COPY ./build/libs/*-all.jar /app/bikenance-backend.jar

ENTRYPOINT ["java","-jar","/app/bikenance-backend.jar"]