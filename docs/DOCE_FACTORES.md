# Metodología 12-Factor App — Implementación y Evidencias Técnicas

**Proyecto:** LocalSpot Backend  
**Framework:** Spring Boot 3.3.5 / Java 17  
**Infraestructura:** Docker Compose, PostgreSQL 16  
**Fecha:** Noviembre 2026  

---

## Introducción

La metodología **The Twelve-Factor App** (12 Factors) es un conjunto de mejores prácticas para construir aplicaciones modernas tipo software-as-a-service (SaaS) preparadas para despliegues en la nube, escalables, resilientes y portables. 

A continuación se detalla la matriz completa con la justificación arquitectónica y las evidencias en el código de **LocalSpot Backend** para cada uno de los 12 factores.

---

## Tabla Factor / Implementación con Evidencias

| # | Factor | Principio Central | Implementación en LocalSpot Backend | Evidencia Técnica en el Proyecto |
| :---: | :--- | :--- | :--- | :--- |
| **I** | **Codebase**<br>*(Base de código)* | Un único repositorio controlado por versiones, múltiples despliegues. | Un único repositorio Git que contiene el código fuente, configuración base, migraciones y manifiestos de contenedores. Todos los despliegues (local, Docker, CI/CD) derivan del mismo código. | [`.git/`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/.git), [`.github/workflows/`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/.github/workflows), [`pom.xml`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/pom.xml). |
| **II** | **Dependencies**<br>*(Dependencias)* | Declarar y aislar explícitamente todas las dependencias sin asumir herramientas del sistema. | Gestión determinística mediante Maven Wrapper (`mvnw`), declarando dependencias con versiones fijas en `pom.xml`. En Docker se pre-descargan en una etapa aislada (`dependency:go-offline`). | [`pom.xml:L43-L172`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/pom.xml#L43-L172), [`Dockerfile:L4-L6`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/Dockerfile#L4-L6). |
| **III** | **Config**<br>*(Configuración)* | Guardar la configuración que varía entre entornos en variables de entorno del sistema operativo. | Cero credenciales o URLs hardcodeadas en código. `application.yml` lee variables de entorno de sistema (`${DB_URL}`, `${JWT_SECRET}`, `${AI_API_KEY}`) con valores por defecto seguros para desarrollo. | [`application.yml:L5-L15`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/application.yml#L5-L15), [`docker-compose.yml:L51-L85`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docker-compose.yml#L51-L85). |
| **IV** | **Backing Services**<br>*(Servicios de apoyo)* | Tratar a las bases de datos, colas y APIs externas como recursos conectables por URL. | PostgreSQL, Mailpit y Google Gemini son tratados como recursos intercambiables vía URLs de conexión. Si la base de datos cambia de host o puerto, solo se actualiza `DB_URL` sin tocar el código compilado. | [`docker-compose.yml:L53`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docker-compose.yml#L53), [`GeminiAiSearchAdapter.java:L39-L43`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/infrastructure/adapter/out/ai/GeminiAiSearchAdapter.java#L39-L43). |
| **V** | **Build, Release, Run**<br>*(Construir, publicar, ejecutar)* | Separar estrictamente las etapas de compilación, empaquetado y ejecución. | Multi-stage Dockerfile: La etapa `build` usa Maven + JDK para compilar el `.jar`; la etapa `runtime` usa un JRE Alpine mínimo de solo lectura. Cada imagen construida es inmutable (`localspot-backend:1.0.0`). | [`Dockerfile:L1-L10 (Build)`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/Dockerfile#L1-L10), [`Dockerfile:L11-L33 (Runtime)`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/Dockerfile#L11-L33). |
| **VI** | **Processes**<br>*(Procesos)* | Ejecutar la aplicación como uno o más procesos sin estado (stateless). | Arquitectura 100% Stateless: La sesión no se almacena en memoria de servidor ni en disco; se utiliza autenticación por tokens JWT criptográficos. Los archivos subidos se delegan a un volumen montado. | [`SecurityConfig.java:L48`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/infrastructure/config/SecurityConfig.java) (`SessionCreationPolicy.STATELESS`), [`JwtAuthenticationFilter.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/infrastructure/adapter/out/security/JwtAuthenticationFilter.java). |
| **VII** | **Port Binding**<br>*(Asignación de puertos)* | Exportar servicios autocontenidos mediante vinculación de puertos HTTP. | La aplicación incluye un servidor web embebido (Apache Tomcat) y expone directamente el puerto asignado por la variable `${PORT:8080}` sin requerir un servidor de aplicaciones externo (como WebLogic o WildFly). | [`application.yml:L48`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/application.yml#L48) (`server.port: ${PORT:8080}`), [`Dockerfile:L28`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/Dockerfile#L28) (`EXPOSE 8080`). |
| **VIII** | **Concurrency**<br>*(Concurrencia)* | Escalar horizontalmente mediante el modelo de procesos / hilos. | Modelo de concurrencia optimizado con Tomcat NIO (`server.tomcat.threads.max: 200`) y pool asíncrono `@EnableAsync` para telemetría. Puede escalarse horizontalmente instanciando múltiples réplicas (`docker compose up --scale backend=3`). | [`application.yml:L56-L59`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/application.yml#L56-L59), [`DemoApplication.java:L13`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/DemoApplication.java#L13) (`@EnableAsync`). |
| **IX** | **Disposability**<br>*(Desechabilidad)* | Maximizar robustez con inicio rápido y apagado elegante (Graceful Shutdown). | Configurado `server.shutdown: graceful` para drenar conexiones activas al recibir señales `SIGTERM`/`SIGINT`. El contenedor JVM se detiene limpiamente liberando los recursos de HikariCP. | [`application.yml:L49`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/application.yml#L49) (`shutdown: graceful`), [`Dockerfile:L33`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/Dockerfile#L33) (`exec java`). |
| **X** | **Dev/Prod Parity**<br>*(Paridad desarrollo/producción)* | Mantener desarrollo, staging y producción tan similares como sea posible. | Uso de PostgreSQL 16 y Docker Compose tanto en desarrollo local como en pruebas de rendimiento y staging (evitando diferencias de dialectos entre H2 y PostgreSQL). | [`docker-compose.yml:L2-L20`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docker-compose.yml#L2-L20), [`PostgresIntegrationTest.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/test/java/com/example/demo/PostgresIntegrationTest.java) (Testcontainers con Postgres 16). |
| **XI** | **Logs**<br>*(Logs / Registros)* | Tratar los registros como flujos continuos de eventos (event streams). | La aplicación emite logs estructurados con MDC a `stdout` (consola), y de forma asíncrona persiste telemetría en base de datos (`log_http`). El host o Docker gestiona el stream de salida. | [`application.yml:L147-L148`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/application.yml#L147-L148), [`HttpLoggingFilter.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/infrastructure/logging/HttpLoggingFilter.java). |
| **XII** | **Admin Processes**<br>*(Procesos administrativos)* | Ejecutar tareas de administración/migración como procesos puntuales. | Las migraciones de base de datos se ejecutan automáticamente en el ciclo de vida de la aplicación con Flyway (`db/migration/V1..V12`) sin intervención manual ni scripts sueltos en el servidor. | [`application.yml:L25-L30`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/application.yml#L25-L30), [`src/main/resources/db/migration/`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/db/migration). |

---

## Detalle y Fragmentos de Código de Factores Clave

### Factor III: Configuración en Variables de Entorno
```yaml
# src/main/resources/application.yml
spring:
  datasource:
    url: ${DB_URL:jdbc:postgresql://localhost:5432/localspot_db}
    username: ${DB_USER:postgres}
    password: ${DB_PASSWORD}
    hikari:
      maximum-pool-size: ${DB_MAX_POOL_SIZE:10}
      connection-timeout: ${DB_CONNECTION_TIMEOUT:30000}
```
*Garantiza que el mismo binario pueda ejecutarse en staging, testing o producción simplemente inyectando variables de entorno en el orquestador.*

---

### Factor V: Build, Release, Run con Multi-Stage Dockerfile
```dockerfile
# Etapa 1: BUILD (Compilación aislada)
FROM maven:3.9.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY mvnw pom.xml ./
RUN ./mvnw -B -q -DskipTests dependency:go-offline || true
COPY src ./src
RUN ./mvnw -B -DskipTests clean package

# Etapa 2: RUNTIME (Ejecución inmutable y mínima)
FROM eclipse-temurin:17.0.14_7-jre-alpine AS runtime
WORKDIR /app
COPY --from=build /app/target/*.jar /app/app.jar
USER appuser
ENTRYPOINT ["sh", "-c", "exec java ${JAVA_OPTS} -jar /app/app.jar"]
```
*Evita que compiladores o código fuente lleguen a producción, asegurando que la imagen contenga únicamente el artefacto inmutable y el JRE mínimo.*

---

### Factor IX: Graceful Shutdown y Desechabilidad
```yaml
# src/main/resources/application.yml
server:
  port: ${PORT:8080}
  shutdown: graceful # No interrumpe transacciones en vuelo al detener el contenedor
```
*Cuando Kubernetes o Docker Compose envían `SIGTERM`, Tomcat deja de aceptar solicitudes nuevas y espera a que las solicitudes en curso finalicen antes de cerrar los pools de conexiones y apagar la JVM.*
