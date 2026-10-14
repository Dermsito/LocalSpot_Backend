# LocalSpot Backend Completo - Product Requirements Document

## Overview
- **Summary**: Completar el backend Spring Boot 3.3.5 + Java 17 y la base de datos PostgreSQL 16 del proyecto LocalSpot, plataforma para descubrir lugares poco conocidos según preferencias, categoría, ubicación y presupuesto. Incluye 13 requerimientos funcionales, 11 tablas normalizadas, observabilidad, pruebas unitarias/integrales, colección Postman y documentación de API.
- **Purpose**: Entregar un backend listo para producción consumido por un frontend Angular, cumpliendo estrictamente con Arquitectura Hexagonal y los 11 factores de Twelve-Factor App.
- **Target Users**: Desarrollador frontend Angular (consumidor de API), administradores de LocalSpot, propietarios de establecimientos y usuarios finales.

---

## FASE 0 - TABLA DE AUDITORÍA (Estado Actual)

### A) Tablas de Base de Datos (11 requeridas / 3 existentes)
| # | Tabla | Estado | Detalle de la brecha |
|---|-------|--------|----------------------|
| 1 | `ROL` | ❌ Faltante | Tabla separada para roles (SMALLINT PK) con relación a USUARIO. Actualmente `tipo_usuario` es un enum inline en `usuarios`. |
| 2 | `USUARIO` | ⚠️ Parcial | Existe `usuarios` pero: `id_rol` FK a ROL faltante; nombres deben coincidir exactos; falta `fecha_registro` tipo TIMESTAMP; seed de ADMIN condicional al arranque. |
| 3 | `CATEGORIA` | ⚠️ Parcial | Existe pero `nombre_categoria` debe ser VARCHAR(80), no 50; seed debe ser 5 categorías base; faltan nombres de constraints explícitos (pk_, uk_, etc.) y COMMENT ON. |
| 4 | `LUGAR` | ❌ Faltante | Tabla con 13 campos, FKs a CATEGORIA y USUARIO (propietario nullable), CHECKs de precios, estado_verificacion enum, índices compuestos. |
| 5 | `FOTO_LUGAR` | ❌ Faltante | Tabla de fotos por lugar con URL y fecha. |
| 6 | `CALIFICACION` | ❌ Faltante | PK, FKs a USUARIO y LUGAR, puntuación 1..5, comentario, visible, UNIQUE(usuario,lugar). |
| 7 | `FAVORITO` | ❌ Faltante | PK compuesta (id_usuario, id_lugar), fecha_agregado. |
| 8 | `PREFERENCIA` | ⚠️ Parcial | Existe pero debe migrar nombres: `created_at/updated_at` no se requieren; constraints explícitos con prefijo; CHECKs y FK con ON DELETE controlado. |
| 9 | `RECOMENDACION` | ❌ Faltante | PK, FKs, puntaje_afinidad 0..100, motivo VARCHAR(200), UNIQUE(usuario,lugar). |
| 10 | `REPORTE` | ❌ Faltante | PK, FK a CALIFICACION y USUARIO, motivo, estado enum, fechas, UNIQUE. |
| 11 | `AUDITORIA` | ❌ Faltante | BIGINT PK, FK usuario nullable, entidad, id_registro, accion, datos_anteriores/nuevos JSON TEXT, fecha_accion. |

### B) Requerimientos Funcionales (13 RF)
| RF | Nombre | Estado | Brecha |
|----|--------|--------|--------|
| RF-01 | Registro usuario | ⚠️ Parcial | Registro existe pero sin: rol forzado FINAL; mínimo 8 caracteres validado; integración con tabla ROL; ErrorResponse uniforme con `errors[]`. |
| RF-02 | Login/Logout JWT | ⚠️ Parcial | Login genera token pero: sin authorities por rol real (todos ROLE_USUARIO); respuesta incompleta (falta tipo, expiración, datos usuario); logout endpoint existe pero sin invalidación conceptual; endpoint actual `/api/usuarios/*`, debe reubicarse a `/api/auth/*`. |
| RF-03 | Preferencias CRUD | ⚠️ Parcial | Crear, actualizar, consultar existen pero: falta DELETE; GET debe ser del usuario autenticado no por path; validación min<=max; endpoint en `/api/preferencias` correcto. |
| RF-04 | Búsqueda lugares | ❌ Faltante | Búsqueda textual por nombre+descripción+categoría; solo VERIFICADOS; con calificación promedio; paginación; <3s. |
| RF-05 | Filtros combinables | ❌ Faltante | Categoría, ubicación (GeocodingPort) y rango de precio; cruce con min/max; validación. |
| RF-06 | Detalle lugar | ❌ Faltante | Datos lugar + fotos + lat/long + promedio + comentarios visibles=true. |
| RF-07 | Favoritos | ❌ Faltante | Agregar, quitar, listar sin duplicados. |
| RF-08 | Calificar | ❌ Faltante | 1..5, una por lugar, editable; auditoría de edición. |
| RF-09 | Recomendaciones IA | ❌ Faltante | Puertos AiRecommendationPort, GeocodingPort; puntaje afinidad 0..100; motivo por IA o fallback; sin repetición. |
| RF-10 | Admin CRUD Lugares | ❌ Faltante | Crear/editar/eliminar/verificar-rechazar; actualiza fecha_actualizacion; auditoría; sin huérfanos. |
| RF-11 | Admin CRUD Categorías | ❌ Faltante | Crear/editar/eliminar; nombre único; no eliminar si en uso. |
| RF-12 | Moderación reportes | ❌ Faltante | Usuario reporta calificación; admin lista/acepta/descarta; oculta comentario; auditoría; admin puede ocultar sin reporte. |
| RF-13 | Propietario establecimientos | ❌ Faltante | POST multipart con fotos; estado PENDIENTE; asociado al id_propietario; GET mis lugares. |

### C) Capas de Arquitectura Hexagonal
| Capa | Estado | Observación |
|------|--------|-------------|
| `domain/model` | ⚠️ Parcial | Existen Usuario, Preferencia, Categoria. Faltan 8 modelos. Dominio NO usa frameworks (bien). |
| `domain/port` | ⚠️ Parcial | Existen 5 puertos. Faltan: LugarRepositoryPort, FotoLugar...Port, Calificacion..., Favorito..., Recomendacion..., Reporte..., Auditoria..., AiRecommendationPort, GeocodingPort, StoragePort, RolRepositoryPort. |
| `domain/exception` | ⚠️ Parcial | 6 excepciones. Faltan: LugarNoEncontrado, CalificacionDuplicada, FavoritoDuplicado, ReporteDuplicado, CategoriaEnUsoException, etc. |
| `application/usecase` | ⚠️ Parcial | 5 casos de uso. Faltan ~25 casos de uso nuevos. |
| `infrastructure/adapter/in/web` | ⚠️ Parcial | 3 controllers + GlobalExceptionHandler + DTOs. Faltan controllers para Lugares, Favoritos, Calificaciones, Recomendaciones, Admin, Establecimientos. Faltan DTOs de entrada/salida (~30 DTOs). No usa MapStruct. |
| `infrastructure/adapter/out/persistence` | ⚠️ Parcial | 3 entities, 3 repos, 3 adapters. Faltan 8 entities+repos+adapters JPA. |
| `infrastructure/adapter/out/security` | ⚠️ Parcial | BCrypt, JwtServiceAdapter, JwtAuthenticationFilter. Faltan: authorities por rol real (ROLE_FINAL, ROLE_ADMINISTRADOR, ROLE_PROPIETARIO). |
| `infrastructure/adapter/out/ai | out/geo | out/storage` | ❌ Faltante | Puertos + adaptadores stub (default fallback) para IA, Mapas, Storage. |
| `infrastructure/config` | ⚠️ Parcial | SecurityConfig, OpenApiConfig, UseCaseConfig. Faltan: auditoría listener, admin seed runner, MapStruct config, Resilience4j config, Hikari tuning, logging MDC filter. |

### D) Twelve-Factor App
| Factor | Estado | Brecha |
|--------|--------|--------|
| 1. Base código | ✅ OK | Un solo repo, ramas existen. |
| 2. Dependencias | ✅ OK | pom.xml + mvnw. |
| 3. Configuración | ⚠️ Parcial | application.yml usa ${VAR:default} pero JWT_SECRET y DB_PASSWORD faltan valor por defecto (bien). Faltan: AI_API_KEY, MAPS_API_KEY sin defaults. Falta CORS_ALLOWED_ORIGINS como String parseable a lista. |
| 4. Servicios respaldo | ❌ Faltante | PostgreSQL bien. AiRecommendationPort, GeocodingPort, StoragePort no existen (deben ser reemplazables). |
| 5. Build/Release/Run | ⚠️ Parcial | Dockerfile multi-stage existe. Falta GitHub Actions. |
| 6. Procesos stateless | ⚠️ Parcial | JWT stateless sí. Fotos aún no se almacenan (debe ser por StoragePort, no disco). |
| 7. Puerto | ✅ OK | server.port=${PORT:8080}. |
| 8. Concurrencia | ❌ Faltante | HikariCP sin variables de entorno (máximo pool, timeouts). |
| 9. Desechabilidad | ⚠️ Parcial | Graceful shutdown sí, Actuator health sí. Faltan: liveness/readiness probes mejoradas, Resilience4j (timeouts/reintentos IA y mapas), arranque rápido. |
| 10. Paridad dev/prod | ❌ Faltante | PostgreSQL en todos lados (bien). Faltan: Testcontainers PostgreSQL para tests. ddl-auto=validate (bien). |
| 11. Logs | ❌ Faltante | Logback por defecto. Falta: nivel por LOG_LEVEL, correlation id MDC filter, NUNCA loguear contraseñas/tokens. |
| 12. Admin processes | N/A | No aplica. |

---

## Goals
1. Implementar las 8 tablas faltantes y refactorizar las 3 existentes para cumplir al 100% con el esquema normalizado especificado.
2. Implementar los 13 RF completos, con endpoints en las rutas exactas, códigos HTTP correctos y respuestas uniformes.
3. Cumplir estrictamente Arquitectura Hexagonal: dominio puro (sin Spring/JPA), casos de uso puros, adaptadores con MapStruct para DTOs.
4. Cumplir los 11 factores Twelve-Factor, incluyendo Testcontainers, Resilience4j y logging estructurado con MDC.
5. Pruebas: JUnit unitarias (≥70% dominio) + integración (Testcontainers) flujos clave + ArchUnit (reglas arquitecturales).
6. Colección Postman completa (9 carpetas, tests automáticos, 3 environments), documentación Swagger/OpenAPI y docs/API.md.
7. GitHub Actions: build + test + Docker image. Validación final: `docker compose up -d --build` con todo healthy.

## Non-Goals
- No implementar frontend Angular (trabajo de otro desarrollador).
- No implementar pasarelas de pago, notificaciones push o chat.
- No usar H2, MongoDB, Redis ni motores alternativos a PostgreSQL 16.
- No cambiar la estructura de puertos de Docker actual (host: 15432 DB, 18080 App, 15050 pgAdmin, 3000 Frontend).
- No actualizar Java (permanece en 17) ni Spring Boot (permanece en 3.3.5).

---

## Background & Context
- Repositorio actual en rama `development` con commit `c097bda` (Dockerfile, application.yml, Flyway V1, 3 controladores).
- Infraestructura Docker: `LocalSpot_Trabajo` como carpeta raíz con `docker-compose.yml`, contextos relativos `../LocalSpot_Backend` y `../LocalSpot_Frontend`.
- Dominio actual puro: Usuario, Preferencia, Categoria (sin Spring). Este patrón debe extenderse a los 8 modelos nuevos.
- JWT stateless existente: subject = idUsuario, claims correo y tipoUsuario. Se debe agregar authorities por rol real.
- GlobalExceptionHandler existe pero la estructura ErrorResponse debe migrarse a `{timestamp, status, message, path, errors[]}`.
- Seed: el ADMIN se crea condicionalmente al arranque vía `ADMIN_EMAIL` y `ADMIN_PASSWORD` con BCrypt. Usuarios FINAL y PROPIETARIO de prueba se insertan solo en perfil `dev`.

---

## Functional Requirements

### RF-01 Registro de usuario
- POST `/api/auth/register`. Datos: nombre (≤100), correo (≤150, formato email, único), contraseña (≥8).
- Rol por defecto: FINAL (bloqueado, no se acepta `tipoUsuario` desde el endpoint).
- Contraseña cifrada con BCrypt. Fecha de registro = TIMESTAMP actual.

### RF-02 Login / Logout JWT
- POST `/api/auth/login` → correo + contraseña. Respuesta: `{token, tokenType="Bearer", expiresIn, usuario:{id,nombre,rol}}`.
- POST `/api/auth/logout` → 204 (stateless, cliente borra token).
- Mensaje genérico "Credenciales inválidas" (nunca revela si el correo existe).
- Autorización por 3 roles: ROLE_FINAL, ROLE_ADMINISTRADOR, ROLE_PROPIETARIO.

### RF-03 Preferencias de usuario
- GET `/api/preferencias` (del usuario autenticado, no path variable).
- PUT `/api/preferencias/{id}` y DELETE `/api/preferencias/{id}`.
- Validación: una por categoría; presupuesto_max ≥ 0.
- Respuesta incluye nombre_categoría.

### RF-04 Búsqueda de lugares
- GET `/api/lugares?q=&categoriaId=&ubicacion=&precioMin=&precioMax=&page=&size=`.
- Búsqueda ILIKE en nombre, descripción y categoría. Solo estado_verificacion=VERIFICADO.
- Cada resultado: `{id, nombre, categoria, precioMin, precioMax, calificacionPromedio, latitud, longitud}`.
- Paginación Spring Data: `{content, page, size, totalElements, totalPages}`. Respuesta < 3 s.

### RF-05 Filtros combinables
- Los parámetros de RF-04 son combinables. `precioMin ≤ precioMax` (400 si no).
- `ubicacion` puede ser texto (GeocodingPort convierte a lat/long + radio) o lat,long directamente.
- Cruce de precios: se intersecta `[precioMin query, precioMax query]` con `[precio_min DB, precio_max DB]`.

### RF-06 Detalle de lugar
- GET `/api/lugares/{id}`. Datos completos + lista FOTO_LUGAR + `promedioCalificaciones` + lista CALIFICACION donde visible=true (con nombre usuario y fecha).
- Si el lugar NO es VERIFICADO → el usuario ADMIN o el PROPIETARIO dueño sí lo pueden ver; los demás 404.

### RF-07 Favoritos
- GET `/api/favoritos` → lista del usuario autenticado con info del lugar.
- POST `/api/favoritos/{lugarId}` → agrega (409 si ya existe).
- DELETE `/api/favoritos/{lugarId}` → quita (204).

### RF-08 Calificar lugar
- POST `/api/lugares/{id}/calificaciones` → {puntuacion 1..5, comentario opcional}. UNIQUE(usuario, lugar) → 409 si duplicada.
- PUT `/api/lugares/{id}/calificaciones` → edita la propia. Cada edición INSERTa una fila en AUDITORIA (entidad="CALIFICACION", accion="UPDATE", JSON datos_anteriores/nuevos).

### RF-09 Recomendaciones IA
- Requiere que el usuario tenga al menos una preferencia (400 si no).
- POST `/api/recomendaciones/generar` → filtra VERIFICADOS dentro del presupuesto por categoría. Calcula puntaje_afinidad (0..100): pondera (5 - calificación_promedio_ponderado) * 0.6 + (1 / (1 + total_calificaciones)) * 0.4 (premia baja popularidad + buena nota).
- `motivo`: AiRecommendationPort.generateMotivo(lugar, preferencia) max 200 chars. Si falla (timeout/error) → motivo fallback "Lugar poco conocido que coincide con tu preferencia de {categoria} dentro de tu presupuesto.".
- UNIQUE(id_usuario, id_lugar): regenerar actualiza puntaje y motivo. GET `/api/recomendaciones` lista las últimas del usuario ordenado por puntaje DESC.

### RF-10 Administración - Lugares
- `/api/admin/lugares` (solo ADMINISTRADOR). POST/PUT/DELETE completo. Validación precio_min ≥ 0, precio_max ≥ precio_min.
- PATCH `/api/admin/lugares/{id}/verificacion` → `{estado: VERIFICADO|RECHAZADO}`. Actualiza `fecha_actualizacion`.
- DELETE: confirma (chequea FKs fotos/calificaciones/favoritos/recomendaciones/reportes; borra en cascada controlada o devuelve 409 con mensaje de hijos si se elige RESTRICT).
- Cada cambio INSERT en AUDITORIA (LUGAR, UPDATE/CREATE/DELETE). id_propietario se queda NULL para lugares del admin.

### RF-11 Administración - Categorías
- `/api/admin/categorias` (solo ADMIN). POST nombre único (≤80); PUT; DELETE (409 si hay lugares con esa categoría).

### RF-12 Moderación de reportes
- POST `/api/calificaciones/{id}/reportes` (usuario FINAL cualquiera) → {motivo ≤ 200}. UNIQUE(id_calificacion, id_usuario) → 409. Estado inicial PENDIENTE.
- GET `/api/admin/reportes` (ADMIN) → lista PENDIENTE primero, con info de calificación y usuario reportado.
- PATCH `/api/admin/reportes/{id}` → `{estado: ACEPTADO|DESCARTADO}`. ACEPTADO → oculta CALIFICACION.visible=false + fecha_resolucion = now.
- PATCH `/api/admin/calificaciones/{id}/ocultar` → ADMIN oculta sin reporte (también auditado).
- Todas las acciones INSERT en AUDITORIA (entidad=REPORTE o CALIFICACION, accion=UPDATE).

### RF-13 Propietario - Establecimientos
- POST `/api/establecimientos` multipart/form-data (solo PROPIETARIO): campos lugar + archivos fotos. Fotos se persisten vía StoragePort (URL string). Estado inicial PENDIENTE. id_propietario = usuario autenticado.
- GET `/api/establecimientos/mis-lugares` → solo sus lugares, ordenados por fecha_registro DESC, con estado.

---

## Non-Functional Requirements

### NFR-1 Arquitectura Hexagonal estricta
- `domain/model`: POJOs puros, cero imports de `org.springframework`, `jakarta.persistence`, `io.jsonwebtoken`.
- `application/usecase`: clases públicas con constructor de dependencias (puertos), sin anotaciones `@Component/@Service`.
- `domain/port`: interfaces Java puras; `infrastructure/adapter/out/*` implementan puertos y son `@Component/@Repository`.
- Controllers (`in/web`) reciben DTO validados y solo llaman a un caso de uso. Nunca retornan entities JPA ni modelos dominio.
- MapStruct para mapeos DTO ↔ Modelo ↔ Entity (mappers en `infrastructure/adapter/in/web/mapper` y `out/persistence/mapper`).
- Prueba ArchUnit valida estas reglas en build.

### NFR-2 Twelve-Factor y observabilidad
- Todas las credenciales vía variables de entorno sin defaults: JWT_SECRET, DB_PASSWORD, AI_API_KEY, MAPS_API_KEY.
- Configuración de HikariCP por env vars: máximo pool size, connection timeout, idle timeout.
- Resilience4j: CircuitBreaker + Retry para AiRecommendationPort y GeocodingPort.
- Logging: SLF4J solo stdout; nivel `logging.level.root = ${LOG_LEVEL:INFO}`; un filtro `MDCFilter` agrega `correlationId` por request; NUNCA loguea `contrasena`, `Authorization` header ni `JWT_SECRET`.
- Graceful shutdown + Actuator liveness/readiness probe endpoints.

### NFR-3 Seguridad
- CORS configurado **exclusivamente** dentro del `SecurityFilterChain` (no Bean CorsConfigurationSource independiente). Orígenes desde `CORS_ALLOWED_ORIGINS` (por defecto: `http://localhost:4200,http://localhost:3000`).
- Authorization header `Bearer <token>`. JWT con claims `id_usuario`, `correo`, `rol` (authorities Spring = `ROLE_<ROL>`).
- Reglas de rutas:
  - Públicas: `/api/auth/**`, GET `/api/categorias`, GET `/api/lugares/**` (búsqueda y detalle público VERIFICADOS), `/actuator/health/**`, `/actuator/info`, Swagger.
  - Autenticadas (cualquier rol): `/api/preferencias/**`, `/api/favoritos/**`, `/api/lugares/{id}/calificaciones`, `/api/recomendaciones/**`, `/api/calificaciones/{id}/reportes`.
  - Solo ADMIN: `/api/admin/**`.
  - Solo PROPIETARIO: `/api/establecimientos/**`.
- Códigos HTTP: 401 sin token, 403 con rol equivocado.

### NFR-4 Base de Datos y Flyway
- Solo PostgreSQL 16. Prohibido H2. `ddl-auto=validate`.
- Flyway: `V1__init_schema.sql` se ajusta SOLO si es estrictamente necesario. Los cambios nuevos en `V2__rol_schema.sql`, `V3__lugar_y_relacionadas.sql`, `V4__favoritos_calificaciones.sql`, `V5__reportes_auditoria.sql`, `V6__seed_pruebas.sql`.
- Constraints con nombres explícitos: `pk_<tabla>`, `fk_<tabla>_<campo>`, `uk_<tabla>_<campo>`, `ck_<tabla>_<regla>`, `idx_<tabla>_<campos>`.
- `COMMENT ON TABLE/COLUMN` en español para todas las tablas y columnas.
- Índices: `uk_usuarios_correo` (ya existe), `idx_lugares_verificacion_categoria(estado_verificacion, id_categoria)`, `idx_lugares_precios(precio_min, precio_max)`, `idx_calificaciones_lugar(id_lugar)`, `idx_reporte_estado(estado)`.

### NFR-5 Respuestas uniformes y validación
- `ErrorResponse = {timestamp(ISO), status(int), message(String), path(String), errors:[{field, message}]}`. Los errores de validación Bean populan `errors[]`. Los errores generales dejan `errors: []` y usan `message`.
- Bean Validation en TODO DTO de entrada (@NotBlank, @Size, @Email, @DecimalMin, @PositiveOrZero, @Pattern si aplica).
- Paginación en listados: respuesta `{content, page, size, totalElements, totalPages}`.

### NFR-6 Pruebas y cobertura
- Unitarias (JUnit 5 + Mockito): dominio y casos de uso ≥ 70% líneas cobertura.
- Integración (SpringBootTest + Testcontainers PostgreSQL 16):
  - Registro → login (flujo RF-01/02).
  - Búsqueda + filtros (RF-04/05).
  - Favoritos CRUD (RF-07).
  - Calificar + editar + auditoría (RF-08).
  - Reporte + moderación aceptar (RF-12).
- ArchUnitTest: dominio no accede a infraestructura; usecases sin anotaciones Spring; controllers solo dependen de usecases.
- `mvn clean verify` debe pasar sin skipTests.

### NFR-7 pgAdmin preconfigurado y SQL de verificación
- `docker/pgadmin/servers.json` en el repo LocalSpot_Backend: servidor "LocalSpot DB" host=db port=5432, MaintenanceDB=localspot_db, Username=postgres. SIN password.
- `docs/sql/verificacion.sql`: consultas: lista tablas (11), conteo por tabla, lista FKs, CHECKs, índices, usuarios por rol, lugares por estado_verificacion.
- `docs/sql/diagrama.md`: ER en Mermaid con las 11 tablas y relaciones + cardinalidades.

### NFR-8 Postman, Swagger y documentación
- Swagger UI `/swagger-ui/index.html` con botón Authorize Bearer JWT. Ejemplos para cada endpoint.
- `docs/API.md`: cada endpoint con método, ruta, roles requeridos, ejemplo request (JSON), ejemplo response, códigos HTTP. Usuarios de prueba dev.
- `docs/postman/LocalSpot.postman_collection.json` en 9 carpetas ordenadas (00 Salud … 09 Casos de error).
  - Tests pm.test por petición: status code, estructura JSON clave, búsquedas < 3000 ms.
  - Scripts en login: `pm.environment.set("token", resp.json().token)`; en creaciones: guardar id.
- 2 environments: Local (baseUrl=http://localhost:18080) y Docker-Nginx (baseUrl=http://localhost:3000/api). Variables: token, tokenAdmin, tokenPropietario, lugarId, etc.

### NFR-9 CI/CD GitHub Actions
- `.github/workflows/ci.yml`: checkout → setup Java 17 → `mvn clean verify` → build Docker (build-push-action sin push por defecto). Se ejecuta en push a cualquier rama.

---

## Constraints
- **Technical**: Java 17; Spring Boot 3.3.5; PostgreSQL 16; Maven (mvnw); Flyway; JWT jjwt 0.12.6; Testcontainers; Resilience4j; MapStruct; ArchUnit; SpringDoc OpenAPI 2.6.0; Dockerfile multi-stage Alpine non-root appuser.
- **Business**: Sin valores por defecto para credenciales. 3 roles. Dinero COP. Preferencias una por categoría. Calificación una por lugar. Reporte una por calificación. Recomendación una por lugar.
- **Dependencies**: docker-compose existente NO se rompe. La tabla USUARIO pasa de `tipo_usuario VARCHAR enum inline` a `id_rol SMALLINT FK → ROL`. Adaptar V1 o crear V2+ con ALTER + data migration.

## Assumptions
- StoragePort en RF-13: por ahora implementación `FileSystemStorageAdapter` guardando fotos en un volumen Docker (más fácil probar); se deja preparado el puerto para futuro S3.
- AiRecommendationPort y GeocodingPort: adaptador `StubAiAdapter` (motivo fallback) y `StubGeocodingAdapter` (devuelve coordenadas centro Bogotá para pruebas). Resilience4j envuelve estos stubs; el adapter real se configura con API keys.
- El endpoint actual `/api/usuarios/*` se migra a `/api/auth/*`. Se deja redirect 308 opcional o simplemente se elimina la ruta antigua.
- El seed de 15 lugares VERIFICADOS con fotos y calificaciones va en `V6__seed_pruebas.sql` con inserts idempotentes (ON CONFLICT DO NOTHING).
- El admin se crea en un `ApplicationRunner` que verifica si `ADMIN_EMAIL` está presente; si existe y no hay usuario con ese correo, lo crea con hash BCrypt y rol ADMINISTRADOR.

---

## Acceptance Criteria

### AC-1: Las 11 tablas existen en PostgreSQL con estructura exacta
- **Type**: `rule`
- **Given**: `docker compose up -d db` ejecutado y Flyway corrió
- **When**: se ejecuta el script `docs/sql/verificacion.sql` en pgAdmin
- **Then**: retorna 11 tablas, todas las constraints (pk/fk/uk/ck) existen con los prefijos correctos, índices existen, conteo inicial = 3 roles + 5 categorías + 1 admin (si vars) + 15 lugares seed
- **Pass Condition**: la consulta "lista tablas" devuelve exactamente 11 filas (rol, usuario, categoria, lugar, foto_lugar, calificacion, favorito, preferencia, recomendacion, reporte, auditoria); cada columna coincide tipo/longitud; los COMMENT ON existen.
- **Evidence**: salida capturada de `docs/sql/verificacion.sql` con resultados.

### AC-2: Endpoints RF-01 y RF-02 funcionan con estructura de respuesta correcta y roles en JWT
- **Type**: `rule`
- **Given**: backend levantado healthy
- **When**: Postman ejecuta 01 Auth (register + 3 logins por rol)
- **Then**: registro 201; login FINAL/ADMIN/PROPIETARIO 200; respuesta JSON contiene `token, tokenType, expiresIn, usuario{id,nombre,rol}`; el JWT contiene authorities correctas; endpoint protegido `/api/preferencias` devuelve 401 sin token, 200 con token correcto, 403 con token de otro rol ante `/api/admin/**`.
- **Pass Condition**: todos los pm.test de la carpeta 01 Auth pasan; `GET /api/admin/categorias` con token FINAL → 403; con token ADMIN → 200; con token PROPIETARIO → 403.
- **Evidence**: Newman collection runner carpeta 00-01 PASSED.

### AC-3: Búsqueda y filtros (RF-04/05/06) cumplen rendimiento < 3 s y solo VERIFICADOS
- **Type**: `rule`
- **Given**: 15 lugares seed VERIFICADOS + 2 PENDIENTES insertados
- **When**: `GET /api/lugares?q=pizza&categoriaId=1&precioMin=5000&precioMax=50000&page=0&size=10`
- **Then**: response time < 3000 ms; content solo contiene lugares VERIFICADOS; JSON paginado correcto; cada item tiene `calificacionPromedio` numérico.
- **Pass Condition**: Postman test "Response time < 3000ms" pasa; inspección manual: ningún lugar con estado PENDIENTE o RECHAZADO aparece.
- **Evidence**: Newman output + screenshots si aplica.

### AC-4: Flujo completo RF-07 a RF-13 funciona de extremo a extremo y auditado
- **Type**: `rule`
- **Given**: 3 usuarios de prueba (FINAL, ADMIN, PROPIETARIO) con sus tokens
- **When**: se ejecutan las carpetas 04 Favoritos, 05 Calificaciones, 06 Moderación, 07 Establecimientos, 08 Recomendaciones
- **Then**: Favoritos POST 201, DELETE 204, sin duplicados 409; Calificación POST 201 PUT 200 (INSERT en auditoria); Reporte POST 201 + Admin acepta 200 (visible=false); Establecimiento POST multipart 201 (PENDIENTE, id_propietario=tokenPropietario); Recomendaciones/generar 201 con puntaje 0..100.
- **Pass Condition**: todos los pm.test pasan. Tabla AUDITORIA tiene ≥ 3 filas (UPDATE CALIFICACION, ACEPTAR REPORTE, OCULTAR CALIFICACION).
- **Evidence**: Newman runner completo 00-08 PASSED; consulta `SELECT COUNT(*) FROM auditoria` ≥ 3.

### AC-5: Pruebas unitarias, integración y ArchUnit pasan con cobertura ≥ 70% dominio
- **Type**: `rule`
- **Given**: `mvn clean verify` desde raíz
- **When**: build finaliza
- **Then**: BUILD SUCCESS; 0 failed tests; reporte JaCoCo (agregado) dominio = líneas ≥ 70%; ArchUnit assertions pasan (0 rule violations).
- **Pass Condition**: exit code 0; `target/site/jacoco/index.html` muestra dominio ≥ 70%; surefire reports 0 failures.
- **Evidence**: salida `mvn clean verify` capturada.

### AC-6: Docker compose healthy + non-root + Actuator UP + Swagger accesible
- **Type**: `rule`
- **Given**: `cd LocalSpot_Trabajo ; docker compose up -d --build`
- **When**: esperar 90 s; `docker compose ps`
- **Then**: servicio `app` Estado=healthy; `whoami` en contenedor = appuser; `GET http://localhost:18080/actuator/health` → `{"status":"UP"}`; `GET /actuator/health/liveness` → UP; `GET /actuator/health/readiness` → UP; Swagger UI HTML carga; `/v3/api-docs` JSON 200.
- **Pass Condition**: las 5 condiciones son verdaderas.
- **Evidence**: `docker compose ps`, `docker exec app whoami`, curl actuator/swagger.

### AC-7: Colección Postman completa corre con Collection Runner sin fallos
- **Type**: `rule`
- **Given**: stack docker healthy; environment LocalSpot-Docker-Nginx seleccionado (baseUrl http://localhost:3000/api)
- **When**: Postman Collection Runner ejecuta la colección entera en orden
- **Then**: 0 fallos; todos los pm.test de todas las carpetas pasan; los tokens y IDs se encadenan correctamente entre requests.
- **Pass Condition**: Newman command `newman run docs/postman/LocalSpot.postman_collection.json -e docs/postman/LocalSpot-Docker-Nginx.postman_environment.json` exit code 0.
- **Evidence**: salida Newman.

### AC-8: Cumplimiento Arquitectura Hexagonal (ArchUnit + revisión estática)
- **Type**: `rubric`
- **Dimension**: Adherencia a puertos y adaptadores, pureza del dominio.
- **Scale**: 0-2
- **Anchors**: 0 = dominio usa jakarta.persistence o controllers retornan entidades; 1 = dominio puro pero faltan MapStruct o use cases tienen @Service; 2 = 100%: dominio puro, use cases sin anotaciones, MapStruct en todos los mapeos DTO/Model/Entity, controllers solo llaman casos de uso.
- **Pass Threshold**: ≥ 2
- **Evidence**: ArchUnit test + inspección aleatoria de 5 archivos dominio y 5 usecase.

### AC-9: Logging sin secretos + correlation id + LOG_LEVEL configurable
- **Type**: `rubric`
- **Dimension**: Calidad y seguridad de logs.
- **Scale**: 0-2
- **Anchors**: 0 = aparece "Bearer " o contraseña en los logs; 1 = no hay secretos pero falta MDC correlationId; 2 = no hay secretos, cada request tiene [correlationId] en logs, cambiar LOG_LEVEL=DEBUG produce más salida sin reinicio.
- **Pass Threshold**: ≥ 2
- **Evidence**: grep de logs por "password|Bearer|secret" sin match; 10 líneas de log con correlationId visible; restart con LOG_LEVEL=DEBUG aumenta líneas.

---

## Open Questions
- [ ] ¿Qué ciudad/país usar como centro para el StubGeocodingAdapter? (Asumido: lat=4.7110, lon=-74.0721 = Bogotá, Colombia)
- [ ] ¿Volumen/path para almacenar fotos del StoragePort en desarrollo? (Asumido: `./docker-volumes/lugar-fotos/` en el host, montado en contenedor `/app/uploads`)
- [ ] ¿Mantener compatibilidad con endpoint `/api/usuarios/*` existente? (Asumido: NO, se migra directamente a `/api/auth/*` para alinear con la especificación.)
- [ ] ¿Qué versiones fijas agregar para Resilience4j, MapStruct, ArchUnit, Testcontainers en pom.xml? (Se usarán BOM de Spring Boot 3.3.5 compatibles.)
