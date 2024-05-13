FROM railwayapp/base
WORKDIR /app
COPY target/*.jar app.jar
CMD ["java", "-jar", "app.jar"]