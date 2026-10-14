# LocalSpot Backend Completo - Implementation Tasks

> Derivado de [spec.md](./spec.md). Cada tarea implementa un slice vertical de ACs/TRs.
> Flujo de estado: `pending` → `in_progress` → `completed` (con Completion Evidence).

---

## FASE 1 - Preparación del proyecto

### Task 1.1: Actualizar pom.xml con nuevas dependencias
- **Status**: pending
- **Priority**: high
- **ACs**: AC-5, AC-6
- **Description**: Agregar dependencias y plugins al pom.xml:
  - `spring-boot-starter-validation`
  - `spring-boot-starter-actuator` (verificar)
  - `flyway-core`, `flyway-database-postgresql` (verificar)
  - `springdoc-openapi-starter-webmvc-ui:2.6.0` (verificar)
  - `io.jsonwebtoken:jjwt-api/impl/jackson:0.12.6` (verificar)
  - **NUEVAS**: `org.mapstruct:mapstruct`, `org.mapstruct:mapstruct-processor` (1.6.3), `lombok-mapstruct-binding`
  - **NUEVAS**: `org.springframework.boot:spring-boot-starter-aop` (para Resilience4j)
  - **NUEVAS**: `io.github.resilience4j:resilience4j-spring-boot3`, `resilience4j-circuitbreaker`, `resilience4j-retry` (2.2.0)
  - **TEST**: `org.springframework.boot:spring-boot-starter-test`, `org.testcontainers:postgresql`, `org.testcontainers:junit-jupiter` (BOM 1.20.4)
  - **TEST**: `com.tngtech.archunit:archunit-junit5:1.3.0`
  - **TEST**: `org.springframework.security:spring-security-test`
  - Plugin `jacoco-maven-plugin:0.8.12` prepare-agent + report
  - Plugin `maven-compiler-plugin` con annotationProcessorPaths (lombok, mapstruct, lombok-mapstruct-binding)
- **Test Requirements**:
  - TR-1.1.1 (rule): `mvnw.cmd clean compile` exit code 0 sin excepciones de dependencias faltantes.
  - TR-1.1.2 (rule): `mvnw.cmd dependency:tree` incluye mapstruct, resilience4j, archunit, testcontainers.
- **Stop Condition**: BUILD SUCCESS en compile.

### Task 1.2: Configuración application.yml (Twelve-Factor)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-6
- **Description**: Actualizar `application.yml`:
  - `spring.datasource.hikari.maximum-pool-size=${DB_MAX_POOL_SIZE:10}`, `connection-timeout=${DB_CONNECTION_TIMEOUT:30000}`, `idle-timeout=${DB_IDLE_TIMEOUT:600000}`
  - `spring.flyway.enabled=true`, `locations=classpath:db/migration`, `validate-on-migrate=true`
  - `spring.jpa.hibernate.ddl-auto=validate`, `open-in-view=false`
  - `jwt.secret=${JWT_SECRET:}` SIN DEFAULT, `expiration=${JWT_EXPIRATION:86400000}`
  - `cors.allowed-origins=${CORS_ALLOWED_ORIGINS:http://localhost:4200,http://localhost:3000}`
  - `logging.level.root=${LOG_LEVEL:INFO}`; `logging.level.org.springframework.security=${LOG_LEVEL:INFO}`
  - `management.endpoints.web.exposure.include=health,info,metrics,prometheus`; `management.endpoint.health.probes.enabled=true`; `management.endpoint.health.show-details=when_authorized`
  - `management.health.livenessstate.enabled=true`, `readinessstate.enabled=true`
  - `ai.api-key=${AI_API_KEY:}`, `maps.api-key=${MAPS_API_KEY:}` (sin defaults)
  - `resilience4j.circuitbreaker.instances.aiService` con config (failureRateThreshold=50, waitDurationInOpenState=10s, slidingWindowSize=10)
  - `resilience4j.retry.instances.aiService` (maxAttempts=2, waitDuration=500ms)
  - `resilience4j.circuitbreaker/retry.instances.geoService` similar
  - `app.storage.upload-dir=${UPLOAD_DIR:./uploads}`
  - `app.admin.email=${ADMIN_EMAIL:}`, `app.admin.password=${ADMIN_PASSWORD:}`
  - `server.shutdown=graceful`, `spring.lifecycle.timeout-per-shutdown-phase=30s`
- **Test Requirements**:
  - TR-1.2.1 (rule): La app arranca (`mvnw.cmd spring-boot:run` detenido manualmente o `docker compose`) sin error de configuración.
  - TR-1.2.2 (rule): `GET /actuator/health/liveness` y `/readiness` retornan 200 {"status":"UP"}.
- **Stop Condition**: app arranca y actuator responde.

### Task 1.3: Observabilidad - Logging MDC Filter y seguridad de logs
- **Status**: pending
- **Priority**: medium
- **ACs**: AC-9
- **Description**: Crear `infrastructure/config/MdcFilter.java` (OncePerRequestFilter) que:
  - Extrae `X-Correlation-Id` del header o genera UUID.
  - Lo pone en MDC como `correlationId`.
  - Lo agrega al response header `X-Correlation-Id`.
  - Limpia MDC en finally.
  - Configurar `application.yml` logging pattern: `%d{yyyy-MM-dd HH:mm:ss.SSS} [%thread] [%X{correlationId:-no-cid}] %-5level %logger{36} - %msg%n`
- **Test Requirements**:
  - TR-1.3.1 (rule): Todo request produce log con `[correlationId]` visible.
  - TR-1.3.2 (rule): grep en logs por "password|Bearer|secret" retorna 0 matches.
- **Stop Condition**: filter registrado y log pattern aplicado.

### Task 1.4: Estructura de paquetes y MapStruct config
- **Status**: pending
- **Priority**: high
- **ACs**: AC-8
- **Description**:
  - Crear paquetes faltantes: `domain/model` (Lugar, FotoLugar, Calificacion, Favorito, Recomendacion, Reporte, Auditoria, Rol), `domain/port` (todos los puertos nuevos), `domain/exception` (excepciones nuevas).
  - Crear `infrastructure/config/MapStructConfig.java` con `@MapperConfig(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)`.
  - Crear `infrastructure/adapter/in/web/mapper` y `out/persistence/mapper` (vacíos inicialmente).
- **Test Requirements**:
  - TR-1.4.1 (rule): Estructura de directorios creada verificable con tree/ls.
- **Stop Condition**: directorios creados.

---

## FASE 2 - Base de datos (V1 ajuste + V2..V6)

### Task 2.1: Ajustar V1__init_schema.sql a estándar de nombres + COMMENT
- **Status**: pending
- **Priority**: high
- **ACs**: AC-1
- **Description**: Reescribir `V1__init_schema.sql` para que:
  - `categoria.nombre_categoria` VARCHAR(80) (actual 50).
  - Constraints explícitos: `pk_categoria`, `uk_categoria_nombre`, `pk_usuarios`, `uk_usuarios_correo`, `pk_preferencias`, `fk_preferencias_usuario`, `fk_preferencias_categoria`, `ck_preferencias_presupuesto`.
  - `COMMENT ON TABLE` y `COMMENT ON COLUMN` para categoria, usuarios, preferencias.
  - Seed 5 categorías ON CONFLICT DO NOTHING.
  - `usuarios.fecha_registro` TIMESTAMP DEFAULT CURRENT_TIMESTAMP.
  - **Nota**: dado que V1 ya se ejecutó en algunos entornos, se hará el ajuste con `DROP TABLE IF EXISTS` al inicio (clean) o se documenta que debe limpiarse la DB. Se recomienda `DROP SCHEMA public CASCADE; CREATE SCHEMA public;` al inicio de V1 para garantizar idempotencia en dev.
- **Test Requirements**:
  - TR-2.1.1 (rule): Flyway migrate ejecuta V1 sin errores en DB limpia.
  - TR-2.1.2 (rule): `SELECT column_name, character_maximum_length FROM information_schema.columns WHERE table_name='categoria'` muestra nombre_categoria length=80.
- **Stop Condition**: V1 aplica limpio.

### Task 2.2: V2__rol_y_usuario.sql (tabla ROL + migración USUARIO)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-1, AC-2
- **Description**: Crear `V2__rol_y_usuario.sql`:
  - `CREATE TABLE rol (id SMALLINT PRIMARY KEY, nombre VARCHAR(20) NOT NULL, descripcion VARCHAR(100));` con `pk_rol`, `uk_rol_nombre`, COMMENT.
  - INSERT 3 roles: 1=ADMINISTRADOR, 2=FINAL, 3=PROPIETARIO (ON CONFLICT DO NOTHING).
  - `ALTER TABLE usuarios ADD COLUMN id_rol SMALLINT NOT NULL DEFAULT 2;` (todos pasan a FINAL por defecto).
  - `ALTER TABLE usuarios ADD CONSTRAINT fk_usuarios_rol FOREIGN KEY (id_rol) REFERENCES rol(id);`
  - `CREATE INDEX idx_usuarios_rol ON usuarios(id_rol);`
  - COMMENT.
- **Test Requirements**:
  - TR-2.2.1 (rule): `SELECT * FROM rol` devuelve 3 filas.
  - TR-2.2.2 (rule): `SELECT column_name FROM information_schema.columns WHERE table_name='usuarios' AND column_name='id_rol'` existe.
- **Stop Condition**: V2 aplica.

### Task 2.3: V3__lugar_y_foto_lugar.sql
- **Status**: pending
- **Priority**: high
- **ACs**: AC-1, AC-3
- **Description**: Crear `V3__lugar_y_foto_lugar.sql`:
  - `lugar`: id BIGSERIAL pk_lugar, id_categoria FK fk_lugar_categoria, id_propietario BIGINT nullable FK fk_lugar_propietario → usuarios, nombre VARCHAR(120) NOT NULL, descripcion TEXT, direccion VARCHAR(255), latitud NUMERIC(10,7), longitud NUMERIC(10,7), precio_min NUMERIC(12,2) DEFAULT 0, precio_max NUMERIC(12,2) DEFAULT 0, estado_verificacion VARCHAR(20) DEFAULT 'PENDIENTE', fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP, fecha_actualizacion TIMESTAMP.
  - CHECKs: `ck_lugar_precio_min` (precio_min >= 0), `ck_lugar_precio_max` (precio_max >= precio_min), `ck_lugar_verificacion` (estado_verificacion IN ('PENDIENTE','VERIFICADO','RECHAZADO')).
  - `foto_lugar`: id BIGSERIAL pk_foto_lugar, id_lugar FK fk_foto_lugar_lugar ON DELETE CASCADE, url_foto VARCHAR(500) NOT NULL, fecha_subida TIMESTAMP DEFAULT CURRENT_TIMESTAMP.
  - Índices: `idx_lugares_verificacion_categoria(estado_verificacion, id_categoria)`, `idx_lugares_precios(precio_min, precio_max)`, `idx_foto_lugar_lugar(id_lugar)`.
  - COMMENT para todo.
- **Test Requirements**:
  - TR-2.3.1 (rule): `information_schema.tables` contiene lugar y foto_lugar.
  - TR-2.3.2 (rule): `pg_constraint` contiene los 3 ck_lugar*.
- **Stop Condition**: V3 aplica.

### Task 2.4: V4__favoritos_calificaciones_recomendacion.sql
- **Status**: pending
- **Priority**: high
- **ACs**: AC-1, AC-4
- **Description**: Crear `V4__favoritos_calificaciones_recomendacion.sql`:
  - `favorito`: id_usuario FK, id_lugar FK, PK compuesta pk_favorito, fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP. ON DELETE CASCADE en ambos.
  - `calificacion`: id BIGSERIAL pk, id_usuario FK, id_lugar FK, puntuacion SMALLINT, comentario TEXT, visible BOOLEAN DEFAULT TRUE, fecha_registro TIMESTAMP, fecha_actualizacion TIMESTAMP. UNIQUE(usuario,lugar) uk_calificacion_usuario_lugar. CHECK ck_calificacion_puntuacion (puntuacion BETWEEN 1 AND 5).
  - `recomendacion`: id BIGSERIAL pk, id_usuario FK, id_lugar FK, puntaje_afinidad NUMERIC(5,2), motivo VARCHAR(200), fecha_generacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP. UNIQUE(usuario,lugar). CHECK ck_recomendacion_puntaje (puntaje_afinidad BETWEEN 0 AND 100).
  - Índices: idx_calificaciones_lugar, idx_recomendacion_usuario.
  - COMMENT.
- **Test Requirements**:
  - TR-2.4.1 (rule): Las 3 tablas existen.
  - TR-2.4.2 (rule): UNIQUE constraints existen en calificacion y recomendacion.
- **Stop Condition**: V4 aplica.

### Task 2.5: V5__reporte_auditoria.sql
- **Status**: pending
- **Priority**: high
- **ACs**: AC-1, AC-4
- **Description**: Crear `V5__reporte_auditoria.sql`:
  - `reporte`: id BIGSERIAL pk, id_calificacion FK fk_reporte_calificacion ON DELETE CASCADE, id_usuario FK fk_reporte_usuario ON DELETE CASCADE, motivo VARCHAR(200) NOT NULL, estado VARCHAR(20) DEFAULT 'PENDIENTE', fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP, fecha_resolucion TIMESTAMP. UNIQUE(calificacion, usuario) uk_reporte_calif_usuario. CHECK ck_reporte_estado (estado IN ('PENDIENTE','ACEPTADO','DESCARTADO')).
  - `auditoria`: id BIGSERIAL pk_auditoria, id_usuario BIGINT nullable FK fk_auditoria_usuario ON DELETE SET NULL, entidad VARCHAR(50) NOT NULL, id_registro BIGINT, accion VARCHAR(20) NOT NULL, datos_anteriores TEXT, datos_nuevos TEXT, fecha_accion TIMESTAMP DEFAULT CURRENT_TIMESTAMP. CHECK ck_auditoria_accion (accion IN ('CREATE','UPDATE','DELETE')).
  - Índices: idx_reporte_estado, idx_auditoria_entidad.
  - COMMENT.
- **Test Requirements**:
  - TR-2.5.1 (rule): Las 2 tablas existen con sus constraints.
- **Stop Condition**: V5 aplica.

### Task 2.6: V6__seed_pruebas.sql
- **Status**: pending
- **Priority**: medium
- **ACs**: AC-3, AC-7
- **Description**: Crear `V6__seed_pruebas.sql` (solo perfil dev - se ejecuta siempre pero con ON CONFLICT):
  - 15 lugares VERIFICADOS con categoría, precios, lat/long Bogotá, descripciones.
  - 2 lugares PENDIENTES.
  - 30 fotos (2 por lugar verificado).
  - Calificaciones de prueba (3 usuarios FINAL x 5 lugares cada uno = 15 calificaciones con puntuaciones variadas y comentarios).
  - Usuarios de prueba FINAL/PROPIETARIO con BCrypt hash conocida (password 'password123' o similar DEV ONLY).
  - Todo con ON CONFLICT DO NOTHING.
- **Test Requirements**:
  - TR-2.6.1 (rule): `SELECT COUNT(*) FROM lugar WHERE estado_verificacion='VERIFICADO'` = 15.
  - TR-2.6.2 (rule): `SELECT COUNT(*) FROM calificacion` >= 15.
- **Stop Condition**: V6 aplica y datos sembrados.

---

## FASE 3 - Dominio (modelos + puertos + excepciones)

### Task 3.1: Modelos de dominio puros
- **Status**: pending
- **Priority**: high
- **ACs**: AC-8
- **Description**: Crear en `domain/model`:
  - `Rol.java` (id Short, nombre, descripcion)
  - `Lugar.java` (id, categoriaId, propietarioId, nombre, descripcion, direccion, latitud BigDecimal, longitud BigDecimal, precioMin BigDecimal, precioMax BigDecimal, estadoVerificacion enum, fechaRegistro, fechaActualizacion)
  - `EstadoVerificacion.java` enum (PENDIENTE, VERIFICADO, RECHAZADO)
  - `FotoLugar.java` (id, lugarId, urlFoto, fechaSubida)
  - `Calificacion.java` (id, usuarioId, lugarId, puntuacion Short, comentario, visible Boolean, fechaRegistro, fechaActualizacion)
  - `Favorito.java` (usuarioId, lugarId, fechaAgregado)
  - `Recomendacion.java` (id, usuarioId, lugarId, puntajeAfinidad BigDecimal, motivo, fechaGeneracion)
  - `Reporte.java` (id, calificacionId, usuarioId, motivo, estado EstadoReporte, fechaCreacion, fechaResolucion)
  - `EstadoReporte.java` enum (PENDIENTE, ACEPTADO, DESCARTADO)
  - `Auditoria.java` (id, usuarioId, entidad, idRegistro, accion, datosAnteriores, datosNuevos, fechaAccion)
  - **Refactor**: `Usuario.java` agregar campo `rolId` (Short) y quitar/conservar `tipoUsuario` (se migra a rolId).
  - **Refactor**: `Categoria.java` sin cambios estructurales.
  - Todos POJOs puros: cero imports de `org.springframework`, `jakarta.*`, `io.jsonwebtoken`.
- **Test Requirements**:
  - TR-3.1.1 (rule): `javap` o inspección muestra que los modelos NO importan paquetes de framework.
  - TR-3.1.2 (rule): ArchUnit rule `noClasses().that().resideInAPackage("..domain.model..").should().dependOnClassesThat().resideInAnyPackage("org.springframework..", "jakarta..")` pasa.
- **Stop Condition**: todos los modelos compilan y son puros.

### Task 3.2: Puertos de salida (repository ports + service ports)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-8
- **Description**: Crear en `domain/port`:
  - `RolRepositoryPort` (findById, findAll)
  - `LugarRepositoryPort` (save, findById, findByEstadoVerificacion, buscarConFiltros con Pageable, updateVerificacion, deleteById, existsByCategoriaId)
  - `FotoLugarRepositoryPort` (saveAll, findByLugarId, deleteByLugarId)
  - `CalificacionRepositoryPort` (save, findByLugarId, findByUsuarioIdAndLugarId, updateVisible, existsByUsuarioIdAndLugarId)
  - `FavoritoRepositoryPort` (save, deleteByUsuarioIdAndLugarId, findByUsuarioId, existsByUsuarioIdAndLugarId)
  - `RecomendacionRepositoryPort` (save, findByUsuarioIdOrderByPuntajeDesc, existsByUsuarioIdAndLugarId, findByUsuarioIdAndLugarId)
  - `ReporteRepositoryPort` (save, findAllOrderByEstado, findById, updateEstado)
  - `AuditoriaRepositoryPort` (save)
  - `AiRecommendationPort` (generateMotivo(lugar, preferencia) String)
  - `GeocodingPort` (geocode(ubicacion String) Coordenadas record)
  - `StoragePort` (store(MultipartFile) String url, delete(String url))
  - `AuditoriaPort` (registrar(usuarioId, entidad, idRegistro, accion, datosAnteriores, datosNuevos))
- **Test Requirements**:
  - TR-3.2.1 (rule): Todos los puertos son interfaces Java sin anotaciones.
- **Stop Condition**: interfaces creadas.

### Task 3.3: Excepciones de dominio
- **Status**: pending
- **Priority**: high
- **ACs**: AC-2, AC-4
- **Description**: Crear en `domain/exception`:
  - `LugarNoEncontradoException` (404)
  - `CalificacionDuplicadaException` (409)
  - `FavoritoDuplicadoException` (409)
  - `ReporteDuplicadoException` (409)
  - `CategoriaEnUsoException` (409)
  - `RecomendacionSinPreferenciasException` (400)
  - `PresupuestoInvalidoException` (400)
  - `VerificacionInvalidaException` (400)
  - `EstadoReporteInvalidoException` (400)
- **Test Requirements**:
  - TR-3.3.1 (rule): Cada excepción extiende RuntimeException y tiene un código HTTP asociado o el GlobalExceptionHandler la mapea.
- **Stop Condition**: excepciones creadas.

---

## FASE 4 - Persistencia (entities JPA, repos, adapters, mappers)

### Task 4.1: Entities JPA + repos Spring Data
- **Status**: pending
- **Priority**: high
- **ACs**: AC-1
- **Description**: Crear en `infrastructure/adapter/out/persistence/entity`:
  - `RolJpaEntity`, `LugarJpaEntity` (con @ManyToOne categoria, @ManyToOne propietario optional), `FotoLugarJpaEntity`, `CalificacionJpaEntity`, `FavoritoJpaEntity` (con @EmbeddedId o IdClass), `RecomendacionJpaEntity`, `ReporteJpaEntity`, `AuditoriaJpaEntity`.
  - `FavoritoId` (id class) o `@EmbeddedId FavoritoEmbeddable`.
  - Repos Spring Data en `repository/`: `RolJpaRepository`, `LugarJpaRepository` (con método `findByEstadoVerificacionAnd...` y `@Query` para filtros), `FotoLugarJpaRepository`, `CalificacionJpaRepository`, `FavoritoJpaRepository`, `RecomendacionJpaRepository`, `ReporteJpaRepository`, `AuditoriaJpaRepository`.
  - **Refactor**: `UsuarioJpaEntity` agregar `@ManyToOne RolJpaEntity rol` (o `@Column id_rol` + join en adapter). Mantener mapeo limpio.
- **Test Requirements**:
  - TR-4.1.1 (rule): Hibernate `ddl-auto=validate` no reporta columnas faltantes (app arranca).
  - TR-4.1.2 (rule): `LugarJpaRepository` tiene métodos de búsqueda con filtros.
- **Stop Condition**: entities y repos creados, app arranca con validate.

### Task 4.2: Mappers MapStruct (Entity ↔ Modelo dominio)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-8
- **Description**: Crear mappers en `infrastructure/adapter/out/persistence/mapper`:
  - `RolMapper`, `LugarMapper`, `FotoLugarMapper`, `CalificacionMapper`, `FavoritoMapper`, `RecomendacionMapper`, `ReporteMapper`, `AuditoriaMapper`.
  - Cada `@Mapper(componentModel="spring")` con métodos `toDomain(entity)` y `toEntity(domain)`.
  - Para `Favorito` (PK compuesta): mapear usuarioId/lugarId desde/hacia el Embeddable.
- **Test Requirements**:
  - TR-4.2.1 (rule): Los mappers generan implementación (compile genera target/generated-sources con *MapperImpl).
  - TR-4.2.2 (rule): Un mapper de prueba round-trip entity→domain→entity conserva campos clave.
- **Stop Condition**: mappers compilan y generan implementación.

### Task 4.3: Adapters de persistencia (implementan puertos)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-8
- **Description**: Crear en `infrastructure/adapter/out/persistence/adapter`:
  - `RolRepositoryAdapter`, `LugarRepositoryAdapter` (con query dinámica usando Specification o JPQL con Criteria para filtros combinables), `FotoLugarRepositoryAdapter`, `CalificacionRepositoryAdapter`, `FavoritoRepositoryAdapter`, `RecomendacionRepositoryAdapter`, `ReporteRepositoryAdapter`, `AuditoriaRepositoryAdapter`.
  - Cada `@Repository` implementa el puerto correspondiente, inyecta JpaRepository y Mapper.
- **Test Requirements**:
  - TR-4.3.1 (rule): Cada adapter implementa el port correcto (verificable por `implements`).
- **Stop Condition**: adapters creados.

---

## FASE 5 - Seguridad (roles reales, auth refactor)

### Task 5.1: Cargar roles reales en JWT y authorities
- **Status**: pending
- **Priority**: high
- **ACs**: AC-2
- **Description**:
  - `JwtServiceAdapter`: método `generateToken(usuario)` que incluye claim `rol` = nombre rol (ADMINISTRADOR/FINAL/PROPIETARIO) y authorities = `ROLE_<ROL>`.
  - `JwtAuthenticationFilter`: extrae `rol` del token y crea `UsernamePasswordAuthenticationToken` con `SimpleGrantedAuthority("ROLE_" + rol)`.
  - `CustomUserDetailsService` o similar: en login, cargar usuario + rol desde DB y validar BCrypt.
- **Test Requirements**:
  - TR-5.1.1 (rule): Login de ADMIN retorna JWT que al decodificar tiene rol=ADMINISTRADOR.
  - TR-5.1.2 (rule): `GET /api/admin/categorias` con token ADMIN → 200; con token FINAL → 403.
- **Stop Condition**: authorities por rol funcionan.

### Task 5.2: Refactor endpoints auth `/api/usuarios/*` → `/api/auth/*`
- **Status**: pending
- **Priority**: high
- **ACs**: AC-2
- **Description**:
  - Crear `AuthController` en `infrastructure/adapter/in/web` con `POST /api/auth/register`, `POST /api/auth/login`, `POST /api/auth/logout`.
  - Crear DTOs: `LoginResponse` (token, tokenType, expiresIn, usuario{id, nombre, rol}).
  - Migrar lógica de `UsuarioController` (registro/login/logout) a casos de uso `RegistrarUsuarioUseCase`, `LoginUseCase` (ya existen) y nuevo `LogoutUseCase` (simple, devuelve void).
  - Actualizar `SecurityConfig`: `.requestMatchers("/api/auth/**").permitAll()` y quitar `/api/usuarios/**` de permitAll (dejar `/api/usuarios/registro` removido o deprecated).
  - Eliminar o vaciar `UsuarioController` (si tenía rutas de perfil, moverlas).
- **Test Requirements**:
  - TR-5.2.1 (rule): `POST /api/auth/register` 201, `POST /api/auth/login` 200 con estructura LoginResponse.
  - TR-5.2.2 (rule): `POST /api/auth/logout` 204.
- **Stop Condition**: endpoints auth en rutas nuevas funcionan.

### Task 5.3: SecurityConfig con todas las reglas de autorización
- **Status**: pending
- **Priority**: high
- **ACs**: AC-2, AC-3, AC-4
- **Description**: Actualizar `SecurityConfig` con:
  - CORS integrado en el filter chain usando `CORS_ALLOWED_ORIGINS` parseado a lista.
  - Rutas públicas: `/api/auth/**`, `GET /api/categorias`, `GET /api/lugares/**`, `/actuator/health/**`, `/actuator/info`, `/swagger-ui/**`, `/v3/api-docs/**`.
  - Autenticadas: `/api/preferencias/**`, `/api/favoritos/**`, `/api/lugares/*/calificaciones`, `/api/recomendaciones/**`, `/api/calificaciones/*/reportes`.
  - `hasRole("ADMINISTRADOR")`: `/api/admin/**`.
  - `hasRole("PROPIETARIO")`: `/api/establecimientos/**`.
  - csrf disabled, stateless session.
- **Test Requirements**:
  - TR-5.3.1 (rule): Matriz de rutas: pública sin token 200, autenticada sin token 401, admin con token final 403.
- **Stop Condition**: matriz de autorización correcta.

---

## FASE 6 - Adaptadores externos (IA, Geo, Storage, Auditoría)

### Task 6.1: AiRecommendationAdapter (stub + resilience)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-4
- **Description**: Crear `infrastructure/adapter/out/ai/AiRecommendationAdapter` implementando `AiRecommendationPort`:
  - Si `AI_API_KEY` está presente: llamar a endpoint (simulado/stub real).
  - Si no o falla: devolver motivo fallback: `String.format("Lugar poco conocido que coincide con tu preferencia de %s dentro de tu presupuesto.", categoriaNombre)`.
  - Anotar método con `@CircuitBreaker(name="aiService", fallbackMethod="fallbackMotivo")` y `@Retry(name="aiService")`.
- **Test Requirements**:
  - TR-6.1.1 (rule): Motivo generado ≤ 200 caracteres.
  - TR-6.1.2 (rule): Si se fuerza fallo, se usa el motivo fallback.
- **Stop Condition**: adapter con fallback funciona.

### Task 6.2: GeocodingAdapter (stub Bogotá)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-3
- **Description**: Crear `infrastructure/adapter/out/geo/GeocodingAdapter` implementando `GeocodingPort`:
  - `geocode(String ubicacion)`: si ubicación coincide con patrón `lat,long` parsea; si no, devuelve coordenadas centro Bogotá (4.7110, -74.0721) con radio 5 km.
  - Resilience4j circuit breaker similar.
  - `record Coordenadas(BigDecimal latitud, BigDecimal longitud, BigDecimal radioKm)`.
- **Test Requirements**:
  - TR-6.2.1 (rule): `geocode("4.5,-74")` devuelve lat=4.5 lon=-74.
  - TR-6.2.2 (rule): `geocode("Bogotá")` devuelve centro Bogotá.
- **Stop Condition**: geocoding funciona.

### Task 6.3: StorageAdapter (FileSystem)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-4
- **Description**: Crear `infrastructure/adapter/out/storage/FileSystemStorageAdapter` implementando `StoragePort`:
  - `store(file)`: guarda en `app.storage.upload-dir` con nombre UUID + extensión, devuelve URL relativa `/uploads/<uuid>.<ext>`.
  - `delete(url)`: borra archivo.
  - Configurar `WebMvcConfig` para servir `/uploads/**` desde el directorio (con acceso restringido o público según decisión).
- **Test Requirements**:
  - TR-6.3.1 (rule): Subir un archivo devuelve URL y el archivo existe en disco.
  - TR-6.3.2 (rule): delete elimina el archivo.
- **Stop Condition**: storage funciona.

### Task 6.4: AuditoriaAdapter + ApplicationRunner seed ADMIN
- **Status**: pending
- **Priority**: high
- **ACs**: AC-4
- **Description**:
  - `infrastructure/adapter/out/audit/AuditoriaAdapter` implementa `AuditoriaPort` y `AuditoriaRepositoryPort` (o unifica): inserta en tabla auditoria serializando datosAnteriores/nuevos como JSON.
  - `infrastructure/config/AdminDataInitializer.java` (ApplicationRunner): si `ADMIN_EMAIL` y `ADMIN_PASSWORD` están presentes y no existe usuario con ese correo, crea usuario con rol ADMINISTRADOR y contraseña BCrypt.
  - **Nota**: usuarios FINAL/PROPIETARIO de prueba van en V6 seed, no en el runner.
- **Test Requirements**:
  - TR-6.4.1 (rule): Con ADMIN_EMAIL configurado, al arrancar existe usuario con rol ADMINISTRADOR.
  - TR-6.4.2 (rule): `AuditoriaPort.registrar(...)` inserta fila en tabla auditoria.
- **Stop Condition**: auditoría y seed admin funcionan.

---

## FASE 7 - Casos de uso (los 25+ casos)

### Task 7.1: Casos de uso de Lugar (RF-04,05,06,10)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-3, AC-4
- **Description**: Crear en `application/usecase`:
  - `BuscarLugaresUseCase`: recibe filtros (q, categoriaId, ubicacion, precioMin, precioMax, pageable), llama a GeocodingPort si hay ubicacion, construye filtros, consulta LugarRepositoryPort.buscarConFiltros, calcula calificacionPromedio por lugar, retorna Page<LugarResponse>.
  - `ObtenerDetalleLugarUseCase`: lugar + fotos + calificaciones visibles + promedio. Verifica visibilidad (si no es VERIFICADO, solo ADMIN o propietario dueño).
  - `CrearLugarAdminUseCase` (RF-10): valida precios, guarda, audita CREATE.
  - `ActualizarLugarAdminUseCase`: valida, guarda, set fecha_actualizacion, audita UPDATE.
  - `EliminarLugarAdminUseCase`: verifica hijos o borra en cascada, audita DELETE.
  - `ActualizarVerificacionLugarUseCase`: cambia estado, set fecha_actualizacion, audita.
- **Test Requirements**:
  - TR-7.1.1 (rule): Búsqueda solo retorna VERIFICADOS.
  - TR-7.1.2 (rule): Detalle de lugar NO VERIFICADO devuelve 404 a usuario FINAL.
  - TR-7.1.3 (rule): Eliminación de lugar con hijos devuelve 409 con mensaje.
- **Stop Condition**: casos de uso funcionan.

### Task 7.2: Casos de uso de Favorito (RF-07)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-4
- **Description**:
  - `AgregarFavoritoUseCase`: si existe throws FavoritoDuplicadoException.
  - `QuitarFavoritoUseCase`.
  - `ListarFavoritosUseCase`: retorna favoritos con info del lugar.
- **Test Requirements**:
  - TR-7.2.1 (rule): Agregar dos veces el mismo lanza 409.
  - TR-7.2.2 (rule): Listar devuelve solo los del usuario autenticado.
- **Stop Condition**: favoritos funcionan.

### Task 7.3: Casos de uso de Calificacion (RF-08)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-4
- **Description**:
  - `CrearCalificacionUseCase`: valida puntuacion 1..5, verifica no exista, guarda, audita CREATE.
  - `ActualizarCalificacionUseCase`: verifica que sea del usuario, guarda, set fecha_actualizacion, audita UPDATE (con datos anteriores y nuevos JSON).
- **Test Requirements**:
  - TR-7.3.1 (rule): Calificación duplicada 409.
  - TR-7.3.2 (rule): Editar genera fila en auditoria.
- **Stop Condition**: calificaciones funcionan.

### Task 7.4: Casos de uso de Recomendacion (RF-09)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-4
- **Description**:
  - `GenerarRecomendacionesUseCase`: valida que usuario tenga ≥1 preferencia; busca lugares VERIFICADOS por categoría de preferencia dentro de presupuesto; calcula puntaje afinidad; llama AiRecommendationPort para motivo; guarda (o actualiza si existe) recomendación. Retorna lista generada.
  - `ListarRecomendacionesUseCase`: últimas del usuario por puntaje DESC.
- **Test Requirements**:
  - TR-7.4.1 (rule): Usuario sin preferencias recibe 400.
  - TR-7.4.2 (rule): Puntaje afinidad entre 0 y 100.
  - TR-7.4.3 (rule): Motivo ≤ 200 caracteres.
- **Stop Condition**: recomendaciones funcionan.

### Task 7.5: Casos de uso de Reporte/Moderación (RF-12)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-4
- **Description**:
  - `CrearReporteUseCase`: valida motivo, verifica no exista, guarda.
  - `ListarReportesPendientesUseCase`: admin, ordenados PENDIENTE primero.
  - `ResolverReporteUseCase`: actualiza estado; si ACEPTADO → Calificacion.visible=false; audita.
  - `OcultarCalificacionAdminUseCase`: admin oculta sin reporte; audita.
- **Test Requirements**:
  - TR-7.5.1 (rule): Reporte duplicado 409.
  - TR-7.5.2 (rule): Aceptar reporte oculta la calificación.
- **Stop Condition**: moderación funciona.

### Task 7.6: Casos de uso de Admin Categorías (RF-11) y Establecimientos (RF-13)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-4
- **Description**:
  - `CrearCategoriaAdminUseCase`, `ActualizarCategoriaAdminUseCase`, `EliminarCategoriaAdminUseCase` (si LugarRepositoryPort.existsByCategoriaId → CategoriaEnUsoException).
  - `CrearEstablecimientoUseCase`: multipart, guarda lugar PENDIENTE con id_propietario=usuario, persiste fotos vía StoragePort, retorna lugar.
  - `ListarMisEstablecimientosUseCase`: del propietario autenticado, fecha_registro DESC.
- **Test Requirements**:
  - TR-7.6.1 (rule): Eliminar categoría en uso 409.
  - TR-7.6.2 (rule): Establecimiento creado tiene estado PENDIENTE e id_propietario correcto.
- **Stop Condition**: categorías admin y establecimientos funcionan.

---

## FASE 8 - Controladores + DTOs + MapStruct entrada/salida

### Task 8.1: DTOs de entrada y salida + mappers
- **Status**: pending
- **Priority**: high
- **ACs**: AC-2, AC-4
- **Description**: Crear DTOs en `infrastructure/adapter/in/web/dto`:
  - Entrada: `BuscarLugaresRequest` (o query params en controller), `CrearLugarRequest`, `ActualizarLugarRequest`, `ActualizarVerificacionRequest`, `CrearCalificacionRequest`, `CrearReporteRequest`, `ResolverReporteRequest`, `CrearCategoriaRequest`, `CrearEstablecimientoRequest` (parte del form-data).
  - Salida: `LugarResponse` (con calificacionPromedio), `LugarDetalleResponse` (con fotos y calificaciones), `FavoritoResponse`, `CalificacionResponse`, `RecomendacionResponse`, `ReporteResponse`, `CategoriaResponse`, `EstablecimientoResponse`, `PageResponse<T>` wrapper.
  - Mappers en `infrastructure/adapter/in/web/mapper`: cada request→modelo y modelo→response.
- **Test Requirements**:
  - TR-8.1.1 (rule): DTOs de entrada tienen Bean Validation annotations.
  - TR-8.1.2 (rule): Mappers generan implementación MapStruct.
- **Stop Condition**: DTOs y mappers creados.

### Task 8.2: Controladores REST
- **Status**: pending
- **Priority**: high
- **ACs**: AC-3, AC-4
- **Description**: Crear controladores:
  - `LugarController`: `GET /api/lugares` (busqueda), `GET /api/lugares/{id}` (detalle), `POST /api/lugares/{id}/calificaciones`, `PUT /api/lugares/{id}/calificaciones`.
  - `FavoritoController`: `GET/POST/DELETE /api/favoritos/**`.
  - `RecomendacionController`: `POST /api/recomendaciones/generar`, `GET /api/recomendaciones`.
  - `ReporteController`: `POST /api/calificaciones/{id}/reportes`.
  - `AdminLugarController`: `/api/admin/lugares/**` (CRUD + PATCH verificacion).
  - `AdminCategoriaController`: `/api/admin/categorias/**`.
  - `AdminReporteController`: `GET /api/admin/reportes`, `PATCH /api/admin/reportes/{id}`, `PATCH /api/admin/calificaciones/{id}/ocultar`.
  - `EstablecimientoController`: `POST /api/establecimientos` (multipart), `GET /api/establecimientos/mis-lugares`.
  - **Refactor**: `PreferenciaController`: GET sin path variable (del usuario autenticado), agregar DELETE.
  - `CategoriaController`: mantener GET público.
- **Test Requirements**:
  - TR-8.2.1 (rule): Cada endpoint responde con código HTTP correcto según RF.
  - TR-8.2.2 (rule): Ningún controller retorna entity JPA o modelo de dominio directamente.
- **Stop Condition**: controllers creados.

### Task 8.3: GlobalExceptionHandler uniforme + ErrorResponse nuevo formato
- **Status**: pending
- **Priority**: high
- **ACs**: AC-2, AC-5
- **Description**:
  - `ErrorResponse = {timestamp(ISO), status(int), message(String), path(String), errors:[{field, message}]}`.
  - `FieldErrorDTO` para errors[].
  - Mapear: MethodArgumentNotValidException → 400 con errors[]; cada excepción de dominio → su código HTTP; AccessDeniedException → 403; AuthenticationException → 401; Exception genérica → 500.
  - Incluir path desde HttpServletRequest.
- **Test Requirements**:
  - TR-8.3.1 (rule): Error de validación retorna estructura con errors[].
  - TR-8.3.2 (rule): Excepción dominio retorna código HTTP correcto.
- **Stop Condition**: handler uniforme.

---

## FASE 9 - Swagger + API.md + pgAdmin servers.json + SQL verificación/diagrama

### Task 9.1: OpenAPI / Swagger con ejemplos
- **Status**: pending
- **Priority**: medium
- **ACs**: AC-7
- **Description**: Configurar `OpenApiConfig` con:
  - `SecurityScheme` Bearer JWT.
  - Anotaciones `@SecurityRequirement(name="bearerAuth")` en controllers protegidos (o global addSecurityItem).
  - `@Operation` + `@ApiResponse` + `@ExampleObject` en endpoints clave (login, busqueda lugares, crear calificacion, generar recomendacion).
  - Info con título "LocalSpot API", versión 1.0.0.
- **Test Requirements**:
  - TR-9.1.1 (rule): Swagger UI carga en `/swagger-ui/index.html`.
  - TR-9.1.2 (rule): Botón Authorize Bearer visible y funcional.
  - TR-9.1.3 (rule): `/v3/api-docs` JSON 200.
- **Stop Condition**: swagger funcional.

### Task 9.2: docs/API.md
- **Status**: pending
- **Priority**: medium
- **ACs**: AC-7
- **Description**: Crear `docs/API.md` con tabla de endpoints: método, ruta, roles, request JSON ejemplo, response JSON ejemplo, códigos HTTP. Agregar sección "Usuarios de prueba (perfil dev)" con los credenciales del seed V6.
- **Test Requirements**:
  - TR-9.2.1 (rule): Archivo existe y cubre los 13 RF.
- **Stop Condition**: API.md creado.

### Task 9.3: docker/pgadmin/servers.json + docs/sql/verificacion.sql + docs/sql/diagrama.md
- **Status**: pending
- **Priority**: medium
- **ACs**: AC-1, AC-7
- **Description**:
  - `docker/pgadmin/servers.json`: servidor "LocalSpot DB", host=db, port=5432, MaintenanceDB=localspot_db, Username=postgres. Sin password.
  - `docs/sql/verificacion.sql`: consultas de verificación (11 tablas, conteos, FKs, CHECKs, índices, usuarios por rol, lugares por estado).
  - `docs/sql/diagrama.md`: ER Mermaid de las 11 tablas con cardinalidades.
- **Test Requirements**:
  - TR-9.3.1 (rule): servers.json válido JSON.
  - TR-9.3.2 (rule): verificacion.sql ejecuta sin errores y devuelve 11 tablas.
- **Stop Condition**: archivos creados.

---

## FASE 10 - Pruebas (unitarias, integración Testcontainers, ArchUnit)

### Task 10.1: Pruebas unitarias de casos de uso (Mockito)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-5
- **Description**: Crear tests unitarios en `src/test/java` para:
  - `RegistrarUsuarioUseCase`, `LoginUseCase`, `BuscarLugaresUseCase`, `CrearCalificacionUseCase`, `GenerarRecomendacionesUseCase`, `CrearReporteUseCase`, `AgregarFavoritoUseCase`, `EliminarCategoriaAdminUseCase`.
  - Cada test mockea puertos, verifica llamadas, cubre caminos felices y excepciones.
- **Test Requirements**:
  - TR-10.1.1 (rule): 0 failures en `mvn test`.
  - TR-10.1.2 (rule): JaCoCo dominio y application ≥ 70% líneas.
- **Stop Condition**: tests pasan con cobertura.

### Task 10.2: Pruebas de integración (Testcontainers PostgreSQL)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-5
- **Description**: Crear `@SpringBootTest` con `@Testcontainers` para flujos:
  - Registro → login (RF-01/02).
  - Búsqueda + filtros (RF-04/05).
  - Favoritos CRUD (RF-07).
  - Calificar + editar + auditoría (RF-08).
  - Reporte + moderación aceptar (RF-12).
  - Usar `@DynamicPropertySource` para setear datasource del contenedor.
- **Test Requirements**:
  - TR-10.2.1 (rule): `mvn verify` pasa con Testcontainers (Docker daemon disponible).
  - TR-10.2.2 (rule): Cada flujo E2E pasa.
- **Stop Condition**: integración pasa.

### Task 10.3: ArchUnitTest (reglas arquitecturales)
- **Status**: pending
- **Priority**: high
- **ACs**: AC-5, AC-8
- **Description**: Crear `ArchitectureTest.java` con reglas:
  - Dominio no depende de Spring/JPA/Web.
  - Casos de uso no tienen anotaciones `@Component/@Service/@Repository`.
  - Controllers (`..in.web..`) solo dependen de `..application.usecase..` y DTOs.
  - Adapters out implementan puertos de `..domain.port..`.
  - Entities JPA residen en `..persistence.entity..`.
- **Test Requirements**:
  - TR-10.3.1 (rule): `mvn test` ArchUnit assertions pasan (0 violations).
- **Stop Condition**: ArchUnit verde.

---

## FASE 11 - GitHub Actions CI + Postman collection + environments

### Task 11.1: .github/workflows/ci.yml
- **Status**: pending
- **Priority**: medium
- **ACs**: AC-7
- **Description**: Workflow:
  - on: push a cualquier rama, pull_request.
  - jobs: build (ubuntu-latest): checkout, setup-java 17, cache maven, `mvn -B clean verify`, build Docker con `docker/build-push-action` (push: false, tags: localspot-backend:ci).
- **Test Requirements**:
  - TR-11.1.1 (rule): YAML sintácticamente válido.
- **Stop Condition**: workflow creado.

### Task 11.2: Postman collection + environments
- **Status**: pending
- **Priority**: high
- **ACs**: AC-7
- **Description**: Crear `docs/postman/LocalSpot.postman_collection.json` con 9 carpetas:
  - 00 Salud (health, info, swagger), 01 Auth (register, login x3 roles, logout), 02 Categorías (GET público, admin CRUD), 03 Lugares (búsqueda, filtros, detalle, admin CRUD, verificación), 04 Favoritos, 05 Calificaciones, 06 Moderación (reporte, admin resolver, ocultar), 07 Establecimientos (POST multipart, mis-lugares), 08 Recomendaciones, 09 Casos de error (401, 403, 404, 409, 400 validación).
  - Cada request con `pm.test`: status code, estructura JSON, response time < 3000ms en búsqueda.
  - Scripts: login setea `token`/`tokenAdmin`/`tokenPropietario`; creaciones guardan `lugarId`, etc.
  - Environments: `LocalSpot-Local.postman_environment.json` (baseUrl=http://localhost:18080) y `LocalSpot-Docker-Nginx.postman_environment.json` (baseUrl=http://localhost:3000/api).
- **Test Requirements**:
  - TR-11.2.1 (rule): JSON collection válido y carga en Postman.
  - TR-11.2.2 (rule): Newman `newman run` exit code 0 contra stack healthy.
- **Stop Condition**: collection y environments creados.

---

## FASE 12 - Validación final (Docker healthy + build + Postman)

### Task 12.1: Validación Docker compose + non-root + Actuator
- **Status**: pending
- **Priority**: high
- **ACs**: AC-6
- **Description**:
  - `docker compose up -d --build` desde `LocalSpot_Trabajo`.
  - Verificar `app` healthy, `docker exec app whoami` = appuser.
  - `curl /actuator/health`, `/health/liveness`, `/health/readiness` = UP.
  - Swagger UI carga.
- **Test Requirements**:
  - TR-12.1.1 (rule): Las 5 condiciones de AC-6 son verdaderas.
- **Stop Condition**: stack healthy.

### Task 12.2: mvn clean verify + cobertura
- **Status**: pending
- **Priority**: high
- **ACs**: AC-5
- **Description**: `mvnw.cmd clean verify` desde raíz. Verificar BUILD SUCCESS, 0 failures, JaCoCo dominio ≥ 70%.
- **Test Requirements**:
  - TR-12.2.1 (rule): exit code 0, dominio ≥ 70%, ArchUnit 0 violations.
- **Stop Condition**: verify verde.

### Task 12.3: Newman Postman completo
- **Status**: pending
- **Priority**: high
- **ACs**: AC-7
- **Description**: `newman run docs/postman/LocalSpot.postman_collection.json -e docs/postman/LocalSpot-Docker-Nginx.postman_environment.json`. Verificar 0 fallos.
- **Test Requirements**:
  - TR-12.3.1 (rule): exit code 0, todos los pm.test pasan.
- **Stop Condition**: Newman PASSED.

---

## AC Coverage Summary
| AC | Tasks que lo cubren |
|----|---------------------|
| AC-1 | 2.1, 2.2, 2.3, 2.4, 2.5, 2.6, 9.3 |
| AC-2 | 5.1, 5.2, 5.3, 8.1, 8.3 |
| AC-3 | 2.3, 7.1, 8.2, 9.1 |
| AC-4 | 2.4, 2.5, 7.1-7.6, 8.2, 6.4 |
| AC-5 | 10.1, 10.2, 10.3, 12.2 |
| AC-6 | 1.1, 1.2, 12.1 |
| AC-7 | 9.1, 9.2, 9.3, 11.2, 12.3 |
| AC-8 | 1.4, 3.1, 3.2, 4.2, 8.1, 10.3 |
| AC-9 | 1.3, 12.1 |
