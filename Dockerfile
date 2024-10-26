FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /workspace/app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
COPY --FROM=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --FROM=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --FROM=build ${DEPENDENCY}/BOOT-INF/classes /app
ENTRYPOINT ["java","-cp","app:app/lib/*","org.breizhcamp.kalon.KalonApplicationKt"]
