FROM alpine/java:21-jre
COPY /target/medilabosolutionapplication_risk-0.0.1-SNAPSHOT.jar risk.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "risk.jar", "--spring.cloud.openfeign.client.gateway.url=http://gateway:8080"] 