FROM alpine/java:21-jre
COPY /target/medilabosolutionapplication_front-1.0.0.jar front.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "front.jar", "--spring.cloud.openfeign.client.gateway.url=http://gateway:8080"]