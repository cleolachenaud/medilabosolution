FROM alpine/java:21-jre
COPY /target/medilabosolutionapplication_notes-1.0.0.jar notes.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "notes.jar", "--spring.data.mongodb.host=host.docker.internal"]