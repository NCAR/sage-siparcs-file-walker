FROM docker.io/eclipse-temurin:11-jdk-jammy
COPY target/WorkingFileVisitor-0.0.1-SNAPSHOT.jar /usr/local/WorkingFileVisitor.jar
WORKDIR /usr/local
CMD ["java","-jar","/usr/local/WorkingFileVisitor.jar"]