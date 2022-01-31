FROM openjdk:11-jdk
EXPOSE 8080
COPY target/currencyconverter-*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]
