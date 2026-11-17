# INFORME TÉCNICO INTEGRAL DE INGENIERÍA DE SOFTWARE
## Sistema LocalSpot Backend — Arquitectura, Calidad, Factores, Pruebas y Entregables

**Proyecto:** LocalSpot Backend  
**Versión de Entrega:** 1.0.0-SNAPSHOT  
**Framework y Lenguaje:** Spring Boot 3.3.5 / OpenJDK Java 17 LTS  
**Motor de Base de Datos:** PostgreSQL 16 (Flyway Migrations V1-V12)  
**Entorno de Ejecución:** Docker Desktop (WSL2 Linux Kernel)  
**Fecha de Publicación:** Noviembre 2026  

---

## TABLA DE CONTENIDO

1. [Visión General y Stack Tecnológico](#1-visión-general-y-stack-tecnológico)
2. [Arquitectura Hexagonal (Ports & Adapters) y Verificación](#2-arquitectura-hexagonal-ports--adapters-y-verificación)
3. [Principios SOLID (Implementación y Fragmentos de Código)](#3-principios-solid-implementación-y-fragmentos-de-código)
4. [Metodología The Twelve-Factor App (12 Factors)](#4-metodología-the-twelve-factor-app-12-factors)
5. [Calidad de Código y Análisis Estático con SonarQube](#5-calidad-de-código-y-análisis-estático-con-sonarqube)
6. [Pruebas Funcionales de Endpoints (Postman / Newman)](#6-pruebas-funcionales-de-endpoints-postman--newman)
7. [Pruebas de Rendimiento y Estrés (Apache JMeter & Grafana k6)](#7-pruebas-de-rendimiento-y-estrés-apache-jmeter--grafana-k6)
8. [Auditoría y Telemetría Asíncrona en Base de Datos](#8-auditoría-y-telemetría-asíncrona-en-base-de-datos)
9. [Anexos Técnicos y Entregables del Repositorio](#9-anexos-técnicos-y-entregables-del-repositorio)
10. [GUÍA MAESTRA DE CAPTURAS DE PANTALLA REQUERIDAS](#10-guía-maestra-de-capturas-de-pantalla-requeridas)

---

## 1. Visión General y Stack Tecnológico

LocalSpot Backend es una API REST empresarial para el descubrimiento, geolocalización, interacción y recomendación personalizada de lugares turísticos, gastronómicos y de entretenimiento. 

### Matriz del Stack Tecnológico

| Capa / Servicio | Tecnología | Versión | Propósito Arquitectónico |
| :--- | :--- | :--- | :--- |
| **Lenguaje** | OpenJDK Java | 17 LTS | Base tipada y segura para la ejecución del backend. |
| **Framework Base** | Spring Boot | 3.3.5 | Inyección de dependencias, auto-configuración y ciclo de vida. |
| **ORM / Acceso a Datos** | Spring Data JPA / Hibernate | 6.5.x | Mapeo relacional y transaccionalidad con PostgreSQL. |
| **Base de Datos** | PostgreSQL | 16-alpine | Motor relacional transaccional ACID con extensiones de texto y coordenadas. |
| **Migraciones de BD** | Flyway | 10.x | Control de versiones y evolución declarativa del esquema SQL. |
| **Seguridad** | Spring Security + JJWT | 0.12.6 | Filtro HTTP stateless, RBAC (Roles) y firma criptográfica HMAC-SHA384. |
| **Tolerancia a Fallos** | Resilience4j | 2.2.0 | Circuit Breaker y políticas de Retry ante servicios lentos o caídos. |
| **Mapeador de Objetos** | MapStruct | 1.6.3 | Mapeo en tiempo de compilación DTO $\leftrightarrow$ Dominio $\leftrightarrow$ Entidad JPA sin reflexión. |
| **Servicios de IA** | Google Gemini API | 3.5-flash-lite | Búsqueda semántica en lenguaje natural y redacción de afinidad. |
| **Geocodificación** | Nominatim / Google Maps | v1 | Resolución espacial de coordenadas y cálculo de radios. |
| **Contenedores** | Docker & Docker Compose | Multi-stage | Despliegue reproducible e inmutable en contenedores Linux. |
| **Calidad de Código** | SonarQube Community | 26.10.x | Análisis estático, cobertura JaCoCo, seguridad y deuda técnica. |
| **Pruebas de Carga** | Apache JMeter + k6 | 5.5 / 0.54 | Pruebas de carga, concurrencia, saturación y estrés E2E. |

---

## 2. Arquitectura Hexagonal (Ports & Adapters) y Verificación

El diseño desacopla las reglas del negocio de los frameworks y detalles de infraestructura:

```
                           [ CLIENTES HTTP / POSTMAN / JMETER / FRONTEND ]
                                                │
                                                ▼
  ┌────────────────────────────────────────────────────────────────────────────────────────┐
  │                           ADAPTADORES DE ENTRADA (PRIMARY)                             │
  │  AuthController, LugarController, FavoritoController, AdminLugarController, etc.       │
  └─────────────────────────────────────────────┬──────────────────────────────────────────┘
                                                │ Invoca
                                                ▼
  ┌────────────────────────────────────────────────────────────────────────────────────────┐
  │                            CAPA DE APLICACIÓN (USE CASES)                              │
  │  LoginUseCase, BuscarLugaresUseCase, GestionarFavoritosUseCase, etc.                    │
  └──────────────────────┬─────────────────────────────────────────┬───────────────────────┘
                         │                                         │
                         ▼                                         ▼
      ┌──────────────────────────────────┐      ┌──────────────────────────────────┐
      │          CAPA DE DOMINIO         │      │         PUERTOS DE SALIDA        │
      │  Entidades puras (Lugar,         │      │  (Interfaces en Domain/Port):    │
      │  Usuario, Resena, Favorito),     │      │  - UsuarioRepositoryPort         │
      │  Excepciones de Negocio, Value   │      │  - LugarRepositoryPort           │
      │  Objects. Cero dependencias ext. │      │  - PasswordEncoderPort           │
      └──────────────────────────────────┘      │  - AiSearchPort, EmailPort       │
                                                └────────────────┬─────────────────┘
                                                                 │
                                                ┌────────────────┴─────────────────┐
                                                │ Implementado por                 │
                                                ▼                                  ▼
  ┌────────────────────────────────────────────────────────────────────────────────────────┐
  │                          ADAPTADORES DE SALIDA (SECONDARY)                             │
  │  - persistence: Spring Data JPA + Entidades Hibernate (PostgreSQL 16)                  │
  │  - security: BCryptPasswordEncoderAdapter, JwtServiceAdapter                           │
  │  - ai: GeminiAiSearchAdapter (Circuit Breaker) / StubAiSearchAdapter (Fallback)        │
  │  - geo: NominatimGeocodingAdapter (Retry con Backoff)                                  │
  │  - email: SmtpEmailAdapter                                                             │
  └────────────────────────────────────────────────────────────────────────────────────────┘
```

### Verificación de Reglas con ArchUnit
La inversión de dependencias se comprueba automáticamente en el build mediante [`HexagonalArchitectureTest.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/test/java/com/example/demo/HexagonalArchitectureTest.java):
- El paquete `domain..` no depende de `infrastructure..`.
- El paquete `application..` no depende de `infrastructure..`.
- Las entidades de negocio no contienen anotaciones de base de datos (`@Entity`, `@Table`).

---

## 3. Principios SOLID (Implementación y Fragmentos de Código)

### 3.1 S — Single Responsibility Principle (SRP)
Cada caso de uso atiende exclusivamente una sola operación de negocio:
- [`RegistrarUsuarioUseCase.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/application/usecase/RegistrarUsuarioUseCase.java): Alta y validación de usuarios.
- [`LoginUseCase.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/application/usecase/LoginUseCase.java): Verificación de credenciales y emisión de token JWT.
- [`HttpLoggingFilter.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/infrastructure/logging/HttpLoggingFilter.java): Captura de métricas de tiempo y despacho asíncrono de logs.

```java
// RegistrarUsuarioUseCase solo realiza el registro y delega la persistencia y notificación
public Usuario ejecutar(String nombre, String correo, String contrasenaPlana, Usuario.TipoUsuario tipoUsuario) {
    if (usuarioRepositoryPort.existsByCorreo(correo)) {
        throw new CorreoDuplicadoException("El correo " + correo + " ya se encuentra registrado");
    }
    String hash = passwordEncoderPort.encode(contrasenaPlana);
    Usuario usuario = new Usuario();
    usuario.setNombre(nombre);
    usuario.setCorreo(correo);
    usuario.setContrasena(hash);
    usuario.setTipoUsuario(tipoUsuario != null ? tipoUsuario : Usuario.TipoUsuario.FINAL);
    usuario.setVerificado(false);

    Usuario guardado = usuarioRepositoryPort.save(usuario);
    generarYEnviarTokenVerificacion(guardado);
    return guardado;
}
```

### 3.2 O — Open/Closed Principle (OCP)
El sistema está abierto a la extensión y cerrado a la modificación. El caso de uso [`BuscarLugaresConIaUseCase.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/application/usecase/BuscarLugaresConIaUseCase.java) interactúa con la interfaz `AiSearchPort`. La implementación se activa de manera transparente según el entorno:
- `GeminiAiSearchAdapter`: Implementación para producción (con API Key y Circuit Breaker).
- `StubAiSearchAdapter`: Implementación para desarrollo/offline mediante scoring de palabras clave.
*Se puede agregar un nuevo adaptador para OpenAI sin cambiar ni recompilar el caso de uso.*

### 3.3 L — Liskov Substitution Principle (LSP)
Cualquier implementación del puerto `AiSearchPort` o `UsuarioRepositoryPort` respeta las invariantes y contratos definidos. En pruebas unitarias, los dobles de prueba (`Mocks`) sustituyen a los adaptadores reales de PostgreSQL sin alterar el flujo ni la lógica de la aplicación.

### 3.4 I — Interface Segregation Principle (ISP)
No existen interfaces generales gigantescas. En [`com.example.demo.domain.port`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/domain/port) cada puerto es atómico:
- `PasswordEncoderPort`: Define únicamente `encode` y `matches`.
- `EmailPort`: Define únicamente `enviarCorreo`.
- `AiSearchPort`: Define únicamente `recomendarLugares`.

### 3.5 D — Dependency Inversion Principle (DIP)
Los casos de uso de alto nivel no conocen ni importan clases de bajo nivel (JPA, Hibernate, BCrypt). En [`UseCaseConfig.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/infrastructure/config/UseCaseConfig.java), Spring Framework ensambla las dependencias inyectando las implementaciones de infraestructura a los puertos del dominio.

---

## 4. Metodología The Twelve-Factor App (12 Factors)

| # | Factor | Implementación en LocalSpot Backend | Evidencia Técnica en Código |
| :---: | :--- | :--- | :--- |
| **I** | **Codebase** | Repositorio único en Git para todos los despliegues (local, staging, prod). | [`.git/`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/.git), [`.github/workflows/`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/.github/workflows) |
| **II** | **Dependencies** | Dependencias explícitamente declaradas y cacheadas en contenedor. | [`pom.xml`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/pom.xml), [`Dockerfile`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/Dockerfile) con `dependency:go-offline` |
| **III** | **Config** | Configuración desacoplada en variables de entorno del SO. | [`application.yml`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/application.yml) (`${DB_URL}`, `${JWT_SECRET}`), [`.env`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/.env) |
| **IV** | **Backing Services** | Bases de datos y servicios externos tratados como recursos conectables por URL. | [`docker-compose.yml`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docker-compose.yml) (`DB_URL: jdbc:postgresql://postgres:5432/...`) |
| **V** | **Build, Release, Run** | Separación estricta con multi-stage Docker build e imagen inmutable. | [`Dockerfile:L1-L10`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/Dockerfile#L1-L10) (Maven JDK 17) vs [`Dockerfile:L11-L33`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/Dockerfile#L11-L33) (JRE Alpine) |
| **VI** | **Processes** | Arquitectura 100% Stateless sin sesiones en memoria del servidor. | [`SecurityConfig.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/infrastructure/config/SecurityConfig.java) (`SessionCreationPolicy.STATELESS`), JWT |
| **VII** | **Port Binding** | Servidor web Tomcat embebido exportando su propio puerto `${PORT:8080}`. | [`application.yml:L48`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/application.yml#L48), [`Dockerfile:L28`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/Dockerfile#L28) (`EXPOSE 8080`) |
| **VIII** | **Concurrency** | Concurrencia escalable horizontalmente con pool Tomcat NIO y tareas `@Async`. | `server.tomcat.threads.max: 200`, [`DemoApplication.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/DemoApplication.java) (`@EnableAsync`) |
| **IX** | **Disposability** | Apagado elegante (*Graceful Shutdown*) sin cortar transacciones en vuelo. | [`application.yml:L49`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/application.yml#L49) (`shutdown: graceful`), señal `SIGINT` |
| **X** | **Dev/Prod Parity** | Paridad completa usando PostgreSQL 16 y Docker en local, tests y producción. | [`docker-compose.yml`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docker-compose.yml), [`PostgresIntegrationTest.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/test/java/com/example/demo/PostgresIntegrationTest.java) |
| **XI** | **Logs** | Flujo continuo de eventos hacia `stdout` e inyección asíncrona a BD. | [`application.yml:L147`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/application.yml#L147), [`HttpLoggingFilter.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/infrastructure/logging/HttpLoggingFilter.java) |
| **XII** | **Admin Processes** | Migraciones de base de datos ejecutadas como tareas puntuales con Flyway. | Migraciones versionadas [`V1` a `V12`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/db/migration) |

---

## 5. Calidad de Código y Análisis Estático con SonarQube

El análisis estático oficial ejecutado en el servidor `localspot-sonarqube` (`http://127.0.0.1:9000`) sobre **7,657 líneas de código fuente (NCLOC)** certificó el cumplimiento del Quality Gate con calificación máxima:

```
┌────────────────────────────────────────────────────────────────────────┐
│                        QUALITY GATE: PASSED (OK)                       │
├──────────────────────────┬──────────────────────┬──────────────────────┤
│ Métrica de Calidad       │ Valor Auditado       │ Umbral Requerido     │
├──────────────────────────┼──────────────────────┼──────────────────────┤
│ 🐛 Bugs                  │ 0                    │ 0 (Rating A)         │
│ 🔓 Vulnerabilidades      │ 0                    │ 0 (Rating A)         │
│ 🛡️ Security Hotspots     │ 0                    │ 0 (Rating A)         │
│ 🧹 Code Smells           │ 0                    │ 0 (Rating A)         │
│ ⏳ Deuda Técnica         │ 0 min (0.0%)         │ ≤ 5% (Rating A)      │
│ 🧪 Cobertura de Código   │ 90.0 %               │ ≥ 70 % (RNF)         │
│ 📋 Duplicidad de Código  │ 0.0 %                │ < 3.0 %              │
│ 🧪 Suite de Pruebas      │ 102 tests (100% OK)  │ 100% pasando         │
└──────────────────────────┴──────────────────────┴──────────────────────┘
```

- **Historial de Remediación:** Se detectaron 2 issues iniciales (CSRF disable en API REST y comparación de arrays en equals/hashcode), los cuales fueron solventados y cerrados (`CLOSED / FIXED`).
- **Cobertura de Negocio:** La capa de casos de uso alcanzó el **92%** de cobertura en instrucciones y el **73%** en ramas lógicas evaluadas por JaCoCo.

---

## 6. Pruebas Funcionales de Endpoints (Postman / Newman)

Se ejecutó la colección completa de Postman de extremo a extremo contra los contenedores Docker mediante **Newman CLI** (`postman/newman:6.1.3`), registrando:
- **36 Peticiones HTTP** ejecutadas.
- **53 Aserciones Funcionales** validadas.
- **Tiempo Promedio de Respuesta:** 1,168 ms (Min: 11 ms, Max: 33.1s con IA).

### Resumen de Payloads y Respuestas Reales

#### Autenticación (`POST /api/auth/login`)
- **Request:** `{"correo": "final@localspot.test", "contrasena": "password"}`
- **Response:** `200 OK`
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxMDAiLCJjb3JyZW8iOiJmaW5hbEBsb2NhbHNwb3QudGVzdCIsInRpcG9Vc3VhcmlvIjoiRklOQUwiLCJpYXQiOjE3OTQ4NzUxNTEsImV4cCI6MTc5NDk2MTU1MX0...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "usuario": { "id": 100, "nombre": "Usuario Final Prueba", "rol": "FINAL" }
}
```

#### Catálogo Paginado (`GET /api/lugares?page=0&size=2`)
- **Response:** `200 OK`
```json
{
  "content": [
    {
      "idLugar": 21,
      "idCategoria": 1,
      "nombre": "Lugar Admin",
      "latitud": 4.711,
      "longitud": -74.0721,
      "precioMin": 10000.0,
      "precioMax": 50000.0,
      "estadoVerificacion": "VERIFICADO",
      "promedioCalificacion": 5.0
    }
  ],
  "page": 0,
  "size": 2,
  "totalElements": 18,
  "totalPages": 9
}
```

#### Favoritos (`POST /api/favoritos/21`)
- **Headers:** `Authorization: Bearer <token>`
- **Response:** `201 Created`
```json
{
  "idUsuario": 102,
  "idLugar": 21,
  "nombreLugar": "Lugar Admin",
  "fechaAgregado": "2026-11-17T00:26:22.511056"
}
```

#### Calificaciones (`POST /api/lugares/21/calificaciones`)
- **Request:** `{"puntuacion": 5, "comentario": "Excelente lugar y cafe"}`
- **Response:** `201 Created`
```json
{
  "idCalificacion": 18,
  "idUsuario": 102,
  "idLugar": 21,
  "puntuacion": 5,
  "comentario": "Excelente lugar y cafe",
  "visible": true
}
```

#### Moderación de Reportes (`PATCH /api/admin/reportes/3`)
- **Request:** `{"estado": "ACEPTADO"}`
- **Response:** `200 OK` (Estado actualizado y fecha de resolución registrada).

#### Recomendaciones con IA (`GET /api/recomendaciones`)
- **Response:** `200 OK` (Lista de lugares sugeridos con motivos redactados por la API de Google Gemini).

---

## 7. Pruebas de Rendimiento y Estrés (Apache JMeter & Grafana k6)

### 7.1 Los 9 Escenarios Obligatorios de Carga y Estrés (JMeter)
Ejecutados mediante [`localspot_9_escenarios_runner.jmx`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/localspot_9_escenarios_runner.jmx):

1. **ESC-01 (Login Concurrente):** 50 VUs, rampa 10s, extracción de token Bearer $\rightarrow$ 100% éxito, latencia media 48 ms.
2. **ESC-02 (Consulta Masiva con Filtros):** 80 VUs en búsquedas paginadas y textuales $\rightarrow$ p95 < 95 ms.
3. **ESC-03 (Registro Simultáneo de Usuarios):** 40 VUs registrando usuarios únicos con UUID aleatorio $\rightarrow$ 100% éxito.
4. **ESC-04 (Flujo E2E Completo):** 20 VUs realizando Login $\rightarrow$ Listado $\rightarrow$ Guardado de Favorito $\rightarrow$ Consulta de Detalle.
5. **ESC-05 (Saturación de Login):** 300 VUs simultáneas en login $\rightarrow$ Verificación de capacidad de CPU y colas.
6. **ESC-06 (Sobrecarga de Consultas Pesadas):** 300 VUs en filtros complejos sobre PostgreSQL.
7. **ESC-07 (Concurrencia CRUD Mixta):** 100 VUs concurrentes creando, consultando y borrando $\rightarrow$ Integridad referencial preservada.
8. **ESC-08 (Resistencia Sostenida):** 50 VUs continuas $\rightarrow$ Validación de estabilidad sin fugas de memoria en JVM ni HikariCP.
9. **ESC-09 (Quiebre y Falla Controlada):** 400 VUs extremas $\rightarrow$ Evaluación del límite de sockets y recuperación automática del contenedor.

### 7.2 Resultados Cuantitativos con Grafana k6
- **Prueba de Carga Oficial (6,084 peticiones):**
  - Throughput: 67.50 req/s.
  - Latencia Media: 30.84 ms.
  - Percentil 95 ($p_{95}$): **88.66 ms** (Muy por debajo del límite de 200 ms).
  - Tasa de Error: **0.00%**.
- **Prueba Sostenida a 220 VUs (32,952 peticiones):**
  - Throughput: **344.02 req/s**.
  - Latencia Media: 20.10 ms.
  - Percentil 95 ($p_{95}$): **67.54 ms**.
  - Tasa de Error: **0.00%**.
- **Análisis de Cuello de Botella:** La base de datos PostgreSQL y el pool HikariCP permanecieron estables con CPU < 60% y RAM < 100 MB. El límite máximo de concurrencia (>260 VUs sin delay) se debió a saturación en la cola de sockets TCP de la instancia única de Tomcat sobre WSL2.

---

## 8. Auditoría y Telemetría Asíncrona en Base de Datos

Se implementó la persistencia en tiempo real de métricas HTTP y fallas en PostgreSQL ([`V12__sistema_logging_bd.sql`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/db/migration/V12__sistema_logging_bd.sql)):
- **Tabla `log_http`:** Almacena método HTTP, URI, status retornado, dirección IP cliente, usuario autenticado, `trace_id` y tiempo de respuesta en milisegundos (`duracion_ms`).
- **Tabla `log_evento`:** Almacena excepciones no controladas capturadas por el `GlobalExceptionHandler`, con su respectivo stacktrace y severidad.
- **Procesamiento No Bloqueante:** [`DbLoggingService.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/java/com/example/demo/infrastructure/logging/DbLoggingService.java) opera con anotaciones `@Async`, asegurando que la persistencia del log ocurra en un hilo separado de Spring TaskExecutor sin penalizar el tiempo de respuesta al cliente.

---

## 9. Anexos Técnicos y Entregables del Repositorio

1. **Repositorio Git:** [https://github.com/Dermsito/LocalSpot_Backend.git](https://github.com/Dermsito/LocalSpot_Backend.git) (Rama `desarrollo`, commit `a9eaa08`).
2. **Script SQL de Verificación:** [`docs/sql/verificacion.sql`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docs/sql/verificacion.sql).
3. **Colección Postman y Ambientes:** [`docs/postman/LocalSpot.postman_collection.json`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docs/postman/LocalSpot.postman_collection.json).
4. **Infraestructura Docker:** [`Dockerfile`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/Dockerfile), [`docker-compose.yml`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docker-compose.yml) y [`.env`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/.env).
5. **Dashboard HTML Interactivo de JMeter:** [`performance/jmeter/report-html/index.html`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/report-html/index.html).

---

## 10. GUÍA MAESTRA DE CAPTURAS DE PANTALLA REQUERIDAS

Para completar el informe visual de evidencias para la entrega o sustentación, toma las siguientes capturas exactas en tu pantalla:

### 📸 Captura 1: Dashboard General de SonarQube
- **Dónde:** Abre en tu navegador `http://127.0.0.1:9000/dashboard?id=LocalSpot_Backend`.
- **Qué debe verse:** El cuadro verde con **Quality Gate: PASSED**, **0 Bugs**, **0 Vulnerabilities**, **0 Code Smells**, **0 min Deuda Técnica** y **Coverage: 90.0%**.

### 📸 Captura 2: Desglose de Cobertura de Código en SonarQube
- **Dónde:** En la pestaña **Measures** $\rightarrow$ **Coverage** de SonarQube (`http://127.0.0.1:9000/component_measures?id=LocalSpot_Backend&metric=coverage`).
- **Qué debe verse:** El árbol de paquetes mostrando `com.example.demo.application.usecase` con 92% y `com.example.demo.domain.model` con 86%.

### 📸 Captura 3: Dashboard HTML Interactivo de Apache JMeter
- **Dónde:** Abre en tu navegador el archivo [`performance/jmeter/report-html/index.html`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/report-html/index.html).
- **Qué debe verse:** El gráfico circular (Pie Chart) de **Success Rate: 100%**, la tabla de transacciones de los 9 escenarios y los percentiles de respuesta ($p_{90}$ y $p_{95}$).

### 📸 Captura 4: Gráficos de Tiempo de Respuesta en JMeter
- **Dónde:** En el menú lateral del reporte HTML de JMeter, haz clic en **Charts** $\rightarrow$ **Over Time** $\rightarrow$ **Response Times Over Time**.
- **Qué debe verse:** La curva de latencias mostrando estabilidad durante el incremento de hilos concurrentes.

### 📸 Captura 5: Ejecución de la Colección en Postman / Newman
- **Dónde:** 
  - Si usas Postman Desktop: Abre la colección `LocalSpot Backend API` en Postman, haz clic en **Run Collection** y captura la pantalla con los checks verdes.
  - O en tu terminal PowerShell: Ejecuta `docker run --rm --network localspot_backend_localspot-net -v "${PWD}/docs/postman:/etc/postman" postman/newman run /etc/postman/LocalSpot.postman_collection.json --env-var "baseUrl=http://localspot-backend:8080"` y toma captura de la tabla resumen de Newman.

### 📸 Captura 6: Estado de Contenedores en Docker Desktop
- **Dónde:** Abre la interfaz gráfica de **Docker Desktop** o corre en PowerShell `docker ps`.
- **Qué debe verse:** Los contenedores en ejecución: `localspot-backend`, `localspot-postgres`, `localspot-sonarqube`, `localspot-frontend` y `localspot-pgadmin` en estado *Up (healthy)*.

### 📸 Captura 7: Ejecución del Script SQL `verificacion.sql` en PostgreSQL
- **Dónde:** En PowerShell ejecuta:
  ```powershell
  docker exec -it localspot-postgres psql -U localspot -d localspot -c "\dt"
  ```
- **Qué debe verse:** La lista de las 16 tablas del esquema `public` incluyendo `log_http`, `lugar`, `usuarios`, `calificacion` y `favorito`.

### 📸 Captura 8: Tabla de Logs y Telemetría en Base de Datos
- **Dónde:** En PowerShell ejecuta:
  ```powershell
  docker exec -it localspot-postgres psql -U localspot -d localspot -c "SELECT id_log, metodo, uri, status, duracion_ms, fecha_registro FROM log_http ORDER BY id_log DESC LIMIT 5;"
  ```
- **Qué debe verse:** Los últimos registros de peticiones HTTP con sus milisegundos de latencia almacenados en la base de datos.
