FROM alpine/java:21-jre
COPY /target/medilabosolutionapplication_patient-1.0.0.jar patient.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "patient.jar", "--spring.datasource.url=jdbc:mysql://host.docker.internal:3306/medilabosolution?serverTimezone=UTC"]