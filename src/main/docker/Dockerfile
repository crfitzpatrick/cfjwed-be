FROM frolvlad/alpine-oraclejdk8
VOLUME /tmp
ADD app-1.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]