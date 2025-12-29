LABEL authors="ajaykumar12"

FROM amazoncorretto:21 AS BUILDER
WORKDIR /app
COPY . .
RUN ./greadlew clean bootjar -x test

FROM amazoncorretto:21-alpine
WORKDIR /app
COPY --from=build /app/builds/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]