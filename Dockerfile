FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY target/*.jar ContactManager.jar
ENTRYPOINT ["java","-jar","/ContactManager.jar"]
EXPOSE 8585