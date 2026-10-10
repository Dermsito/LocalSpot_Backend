FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN ./mvnw -B -q -DskipTests dependency:go-offline || true

COPY src ./src
RUN ./mvnw -B -DskipTests clean package

FROM eclipse-temurin:17.0.14_7-jre-alpine AS runtime
WORKDIR /app

RUN apk add --no-cache --virtual .runtime-deps tzdata wget ca-certificates \
    && addgroup -S appgroup \
    && adduser -S appuser -G appgroup -h /app -s /sbin/nologin -D

ENV TZ=UTC
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 -XX:+ExitOnOutOfMemoryError"

COPY --from=build /app/target/*.jar /app/app.jar

RUN chown -R appuser:appgroup /app \
    && chmod 550 /app/app.jar

USER appuser

EXPOSE 8080

HEALTHCHECK --start-period=60s --interval=30s --timeout=5s --retries=3 \
    CMD wget -qO- --spider http://127.0.0.1:8080/actuator/health/readiness || exit 1

ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/app.jar"]
