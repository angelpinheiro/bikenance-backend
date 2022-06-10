FROM openjdk:11
EXPOSE 8080:8080
ENV DB_HOST localhost
RUN mkdir /app
COPY ./build/libs/*.jar /app/bikenance-backend.jar
ENTRYPOINT ["java","-jar","/app/bikenance-backend.jar"]