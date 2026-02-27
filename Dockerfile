FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY src ./src
COPY web ./web

RUN mkdir -p build/classes \
    && javac -d build/classes $(find src/main/java -name '*.java')

FROM eclipse-temurin:21-jre AS runtime
WORKDIR /app

ENV PORT=8080
ENV PROLOG_DATA_FILE=/app/src/main/resources/prolog/store.pl
ENV DEBUG=false

COPY --from=build /app/build/classes ./build/classes
COPY src/main/resources ./src/main/resources
COPY web ./web

EXPOSE 8080

CMD ["java", "-cp", "build/classes", "com.bruno.salessystem.api.ApiServer"]
