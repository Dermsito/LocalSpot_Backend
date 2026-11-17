# Documento de Arquitectura y Especificación Técnica — LocalSpot Backend

**Proyecto:** LocalSpot Backend  
**Versión:** 1.0.0-SNAPSHOT  
**Framework:** Spring Boot 3.3.5 / Java 17  
**Fecha de Revisión:** Noviembre 2026  
**Entorno de Ejecución:** Contenedores Docker (Linux WSL2 / PostgreSQL 16)

---

## 1. Visión General del Sistema

LocalSpot Backend es una API RESTful empresarial diseñada para la recomendación, descubrimiento, calificación y gestión de lugares de interés y establecimientos comerciales geolocalizados. Integra capacidades avanzadas de autenticación JWT / Google OAuth2, búsqueda vectorial/semántica asistida por IA (Google Gemini), resiliencia de red (Circuit Breakers y Retries) y auditoría/telemetría persistida en base de datos.

### 1.1 Stack Tecnológico Principal

| Componente | Tecnología | Versión | Propósito |
| :--- | :--- | :--- | :--- |
| **Lenguaje** | OpenJDK Java | 17 LTS | Plataforma base de compilación y ejecución |
| **Framework Base** | Spring Boot | 3.3.5 | Inyección de dependencias, MVC, Lifecycle |
| **Persistencia** | Spring Data JPA / Hibernate | 6.5.x | Mapeo objeto-relacional (ORM) |
| **Motor de BD** | PostgreSQL | 16-Alpine | Almacenamiento relacional, índices y persistencia |
| **Migraciones BD** | Flyway | 10.x | Control de versiones y evolución de esquema SQL |
| **Seguridad** | Spring Security + JJWT | 0.12.6 | Filtro stateless, RBAC y firma de tokens |
| **Mapeo de Datos** | MapStruct | 1.6.3 | Mapeo DTO $\leftrightarrow$ Dominio $\leftrightarrow$ Entidad JPA sin reflexión |
| **Tolerancia a Fallos** | Resilience4j | 2.2.0 | Circuit breaker y reintentos ante fallas externas |
| **Documentación** | Springdoc OpenAPI (Swagger) | 2.6.0 | Catálogo interactivo de endpoints OpenAPI 3.0 |
| **Testing** | JUnit 5, Mockito, ArchUnit, Testcontainers | 1.20.4 | Pruebas unitarias, arquitectónicas y de integración |

---

## 2. Arquitectura de Software: Hexagonal (Ports & Adapters)

El backend implementa de forma rigurosa la **Arquitectura Hexagonal (Puertos y Adaptadores)** garantizando la total independencia del modelo de negocio frente a frameworks web, bases de datos y librerías externas.

```
                           [ CLIENTES HTTP / POSTMAN / JMETER / FRONTEND ]
                                                │
                                                ▼
  ┌────────────────────────────────────────────────────────────────────────────────────────┐
  │                           ADAPTADORES DE ENTRADA (PRIMARY)                             │
  │  AuthController, LugarController, FavoritoController, AdminLugarController, etc.       │
  └─────────────────────────────────────────────┬──────────────────────────────────────────┘
                                                │ (Implementa/Invoca)
                                                ▼
  ┌────────────────────────────────────────────────────────────────────────────────────────┐
  │                            CAPA DE APLICACIÓN (USE CASES)                              │
  │  LoginUseCase, BuscarLugaresUseCase, GestionarFavoritosUseCase, etc.                    │
  │  (Orquestación de lógica de negocio, transacciones, manejo de flujo)                   │
  └──────────────────────┬─────────────────────────────────────────┬───────────────────────┘
                         │                                         │
                         ▼                                         ▼
      ┌──────────────────────────────────┐      ┌──────────────────────────────────┐
      │          CAPA DE DOMINIO         │      │         PUERTOS DE SALIDA        │
      │  Entidades puras (Lugar,         │      │  (Interfaces en Domain/Port):    │
      │  Usuario, Resena, Favorito),     │      │  - UsuarioRepositoryPort         │
      │  Excepciones de Negocio, Value   │      │  - LugarRepositoryPort           │
      │  Objects. Cero dependencias ext. │      │  - PasswordEncoderPort           │
      └──────────────────────────────────┘      │  - GeminiAiPort, GeocodingPort   │
                                                └────────────────┬─────────────────┘
                                                                 │
                                                ┌────────────────┴─────────────────┐
                                                │ (Implementado por)               │
                                                ▼                                  ▼
  ┌────────────────────────────────────────────────────────────────────────────────────────┐
  │                          ADAPTADORES DE SALIDA (SECONDARY)                             │
  │  - persistence: Spring Data JPA Repositories + Entidades Hibernate (PostgreSQL)        │
  │  - security: BCryptPasswordEncoder, JwtTokenProvider                                   │
  │  - ai: GeminiAiAdapter (Google Gemini API con CircuitBreaker)                          │
  │  - geo: NominatimGeocodingAdapter (OpenStreetMap con Retry)                            │
  │  - email: JavaMailSenderAdapter                                                        │
  │  - storage: LocalStorageAdapter                                                        │
  └────────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.1 Verificación Arquitectónica con ArchUnit
La integridad de la arquitectura es auditada automáticamente en el pipeline mediante pruebas unitarias con `ArchUnit` ([`HexagonalArchitectureTest.java`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/test/java/com/example/demo/HexagonalArchitectureTest.java)), verificando:
- **Regla 1:** El Dominio no depende de la Infraestructura ni de Spring (`domain.. should not depend on infrastructure..`).
- **Regla 2:** La Aplicación no depende de la Infraestructura directa.
- **Regla 3:** La Inversión de Control se respeta a través de interfaces (`Port`).

---

## 3. Modelo de Dominio y Base de Datos

La base de datos PostgreSQL se gestiona mediante **12 migraciones Flyway versionadas** (`src/main/resources/db/migration/`):

1. **`V1__init_schema.sql`**: Esquema base, tablas principales de categorías y lugares.
2. **`V2__rol_y_usuario.sql`**: Gestión de usuarios (`usuario`), roles (`rol`), relación `usuario_rol`.
3. **`V3__lugar_y_foto_lugar.sql`**: Entidad `lugar` (latitud, longitud, dirección, teléfono) y fotos asociadas.
4. **`V4__favoritos_calificaciones_recomendacion.sql`**: Tablas `favorito`, `calificacion` e historial de recomendaciones.
5. **`V5__reporte_auditoria.sql`**: Sistema de denuncias/reportes de contenido moderable.
6. **`V6__seed_pruebas.sql`**: Datos semilla determinísticos para desarrollo y pruebas.
7. **`V7__lugar_horario.sql`**: Horarios de atención por día de la semana.
8. **`V8__fix_password_hashes.sql`**: Normalización de hashes BCrypt para contraseñas de desarrollo y pruebas.
9. **`V9__verificacion_correo.sql`**: Tokens temporales de confirmación de email.
10. **`V10__recuperacion_contrasena.sql`**: Flujo seguro con OTP/Token temporal para reset de clave.
11. **`V11__indices_rendimiento.sql`**: Índices compuestos optimizados para búsquedas concurrentes y filtros (`idx_lugar_busqueda_completa`, `idx_lugar_categoria_activa`, etc.).
12. **`V12__sistema_logging_bd.sql`**: Esquema de observabilidad `log_http` y `log_evento` para auditoría técnica.

---

## 4. Módulos Funcionales y Casos de Uso

### 4.1 Autenticación y Seguridad (`/api/auth`)
- **Login Local:** Validación de credenciales con BCrypt, emisión de tokens JWT firmados (HMAC-SHA256).
- **Google OAuth2:** Verificación criptográfica de Google ID Tokens y sincronización de perfiles federados.
- **Registro con Verificación de Correo:** Envío asíncrono de correo con código de validación.
- **Recuperación de Contraseña:** Generación de token con expiración y cambio seguro.

### 4.2 Catálogo y Búsqueda de Lugares (`/api/lugares`)
- **Filtros Avanzados:** Búsqueda textual, filtro por categoría, rango de precios, horarios activos y ordenamiento dinámico.
- **Paginación y Metadatos:** Soporte de paginación (`page`, `size`) optimizada para alto rendimiento.
- **Búsqueda Semántica con IA:** Búsqueda en lenguaje natural ("quiero un café tranquilo para estudiar") interpretada por el adaptador `GeminiAiAdapter`.

### 4.3 Interacción de Usuarios
- **Favoritos (`/api/favoritos`):** Adición y remoción atómica con restricción única compuesta (`usuario_id`, `lugar_id`).
- **Calificaciones y Reseñas (`/api/calificaciones`):** Puntuación 1-5 estrellas con actualización del promedio del lugar.
- **Reportes (`/api/reportes`):** Denuncias de información inexacta o comentarios inapropiados.

### 4.4 Panel de Administración (`/api/admin/*`)
- Control de acceso basado en roles (`ROLE_ADMIN`).
- CRUD de categorías, activación/desactivación de lugares y resolución de reportes de auditoría.

---

## 5. Resiliencia, Observabilidad y Telemetría

### 5.1 Tolerancia a Fallos (Resilience4j)
- **Circuit Breaker:** Protege las llamadas externas a la API de Google Gemini (`GeminiAiAdapter`). Si la latencia supera el umbral o la tasa de error alcanza el 50%, el circuito se abre y entrega respuestas en degradación elegante (fallback a búsqueda textual por similitud SQL).
- **Retry:** Implementado en `NominatimGeocodingAdapter` con backoff exponencial para absorber límites transitorios de tasa (Rate Limiting HTTP 429).

### 5.2 Sistema de Logging Asíncrono en Base de Datos
- **Filtro HTTP (`HttpLoggingFilter`):** Intercepta cada solicitud entrante antes y después de su ejecución, calculando el tiempo exacto de procesamiento en milisegundos (`duracion_ms`).
- **Persistencia No Bloqueante (`DbLoggingService` con `@Async`):** Los registros de auditoría y excepciones se despachan a un hilo separado de Spring TaskExecutor, garantizando impacto cero en la latencia de respuesta de los endpoints.
- **MDC (Mapped Diagnostic Context):** Inyección de `traceId` único por hilo para rastreo distribuido de logs en consola y BD.

---

## 6. Aseguramiento de Calidad y Pruebas

```
┌─────────────────────────────────────────────────────────────┐
│                    PIRÁMIDE DE PRUEBAS                      │
├─────────────────────────────────────────────────────────────┤
│  Pruebas de Estrés y Carga E2E (JMeter 9 Escenarios, k6)   │
├─────────────────────────────────────────────────────────────┤
│  Pruebas de Integración con BD Real (Postgres Testcontainers)│
├─────────────────────────────────────────────────────────────┤
│  Pruebas de Reglas de Arquitectura (ArchUnit)               │
├─────────────────────────────────────────────────────────────┤
│  Pruebas Unitarias Aisladas (JUnit 5 + Mockito en Casos     │
│  de Uso y Dominio con cobertura > 80% JaCoCo)               │
└─────────────────────────────────────────────────────────────┘
```

1. **Pruebas Unitarias:** Cobertura de todos los casos de uso principales (`ActualizarPreferenciaUseCaseTest`, `BuscarLugaresUseCaseTest`, etc.).
2. **Pruebas de Carga y Rendimiento (JMeter & k6):**
   - 9 escenarios oficiales ejecutados y reportados en [`docs/PRUEBAS_CARGA_Y_ESTRES.md`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docs/PRUEBAS_CARGA_Y_ESTRES.md).
   - Capacidad comprobada: > 344 req/s sostenidas a 220 VUs sin pérdida de paquetes (0.00% tasa de error).
   - Dashboard HTML navegable en: [`performance/jmeter/report-html/index.html`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/report-html/index.html).

---

## 7. Despliegue y Orquestación

La infraestructura local se ejecuta mediante Docker Compose (`docker-compose.yml`), interconectando:
- `localspot-backend`: Aplicación Java Spring Boot compilada en multi-stage container.
- `localspot-postgres`: Instancia PostgreSQL 16 con volumen persistente de datos.
- `localspot-pgadmin`: Interfaz web de administración de base de datos.
- `localspot-sonarqube`: Servidor de análisis estático de código y deuda técnica.
