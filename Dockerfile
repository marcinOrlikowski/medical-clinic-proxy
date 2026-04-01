FROM eclipse-temurin:21-jdk
MAINTAINER marcinOrlikowski
COPY target/medicalclinicproxy-0.0.1-SNAPSHOT.jar medicalclinicproxy-0.0.1-SNAPSHOT.jar
ENTRYPOINT ["java", "-jar","medicalclinicproxy-0.0.1-SNAPSHOT.jar"]