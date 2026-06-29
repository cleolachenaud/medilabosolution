FROM alpine/java:21-jre
COPY /target/medilabosolutionapplication_security-0.0.1-SNAPSHOT.jar security.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "security.jar", "--spring.cloud.openfeign.client.gateway.url=http://gateway:8080"] 