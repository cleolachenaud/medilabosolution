FROM alpine/java:21-jre
COPY /target/medilabosolutionapplication_gateway-1.0.0.jar gateway.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "gateway.jar"]
