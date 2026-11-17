# Anexos Técnicos y Entregables del Proyecto

**Proyecto:** LocalSpot Backend  
**Fecha de Publicación:** Noviembre 2026  
**Versión de Entrega:** 1.0.0-SNAPSHOT  

---

## 1. Repositorio Oficial y Control de Versiones

| Parámetro | Detalle |
| :--- | :--- |
| **Repositorio Git** | [https://github.com/Dermsito/LocalSpot_Backend.git](https://github.com/Dermsito/LocalSpot_Backend.git) |
| **Rama de Desarrollo Activa** | `desarrollo` |
| **Rama Principal** | `main` |
| **Commit Evaluado** | `a9eaa08` (*perf: habilitar compresion gzip, optimizar tomcat y anadir indices gin y compuestos*) |
| **Pipeline de Automatización** | [`.github/workflows/`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/.github/workflows) |

---

## 2. Scripts de Base de Datos y Verificación

Todos los scripts de evolución de esquema e integridad se encuentran versionados en el proyecto:

### 2.1 Script Maestro de Verificación de Integridad
- **Ruta del Script:** [`docs/sql/verificacion.sql`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docs/sql/verificacion.sql)
- **Propósito:** Comprueba la existencia de tablas, llaves foráneas, usuarios semilla, lugares, promedios de calificación, conteo de favoritos y consistencia de datos en PostgreSQL 16.

**Comando de Ejecución en Docker:**
```powershell
docker exec -i localspot-postgres psql -U localspot -d localspot < docs/sql/verificacion.sql
```

**Resultado de Tablas Verificadas:**
```
       table_name        
-------------------------
 auditoria
 calificacion
 categorias
 favorito
 flyway_schema_history
 foto_lugar
 log_evento
 log_http
 lugar
 preferencias
 recomendacion
 recuperacion_contrasena
 reporte
 rol
 usuarios
 verificacion_correo
(16 rows)
```

### 2.2 Migraciones Versionadas de Flyway
Ruta: [`src/main/resources/db/migration/`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/src/main/resources/db/migration)
1. `V1__init_schema.sql` (Esquema base de categorías y lugares)
2. `V2__rol_y_usuario.sql` (Modelo de usuarios y roles)
3. `V3__lugar_y_foto_lugar.sql` (Geolocalización y fotografías)
4. `V4__favoritos_calificaciones_recomendacion.sql` (Interacciones y afinidad)
5. `V5__reporte_auditoria.sql` (Moderación y denuncias)
6. `V6__seed_pruebas.sql` (Datos semilla de prueba)
7. `V7__lugar_horario.sql` (Horarios de atención)
8. `V8__fix_password_hashes.sql` (Hashes BCrypt para desarrollo)
9. `V9__verificacion_correo.sql` (Tokens de verificación)
10. `V10__recuperacion_contrasena.sql` (Tokens de recuperación)
11. `V11__indices_rendimiento.sql` (Índices compuestos y GIN)
12. `V12__sistema_logging_bd.sql` (Tablas `log_http` y `log_evento`)

---

## 3. Colecciones de Postman y Ambientes

La colección completa de endpoints incluye pruebas funcionales, aserciones y variables de entorno para desarrollo local y Docker:

| Archivo | Ruta | Descripción |
| :--- | :--- | :--- |
| **Colección de Endpoints** | [`docs/postman/LocalSpot.postman_collection.json`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docs/postman/LocalSpot.postman_collection.json) | 36 peticiones HTTP y 53 aserciones para todos los controladores. |
| **Ambiente Docker** | [`docs/postman/LocalSpot-Docker.postman_environment.json`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docs/postman/LocalSpot-Docker.postman_environment.json) | Variables `baseUrl=http://localspot-backend:8080`, tokens JWT de administrador y usuario final. |
| **Ambiente Desarrollo** | [`docs/postman/LocalSpot-Desarrollo.postman_environment.json`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docs/postman/LocalSpot-Desarrollo.postman_environment.json) | Variables `baseUrl=http://localhost:18080` para pruebas desde el host. |

**Comando de Ejecución Automatizada (Newman CLI en Docker):**
```powershell
docker run --rm --network localspot_backend_localspot-net `
  -v "${PWD}/docs/postman:/etc/postman" `
  postman/newman run /etc/postman/LocalSpot.postman_collection.json `
  --env-var "baseUrl=http://localspot-backend:8080"
```

---

## 4. Archivos de Contenedorización e Infraestructura Docker

La orquestación completa de los servicios del sistema se define en:

- **Dockerfile Multi-Stage:** [`Dockerfile`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/Dockerfile)  
  *Etapa `build` (Maven 3.9.9 + Temurin JDK 17) $\rightarrow$ Etapa `runtime` (Temurin JRE 17 Alpine, usuario sin privilegios `appuser`, healthcheck readiness y JVM optimizada).*
- **Orquestador Docker Compose:** [`docker-compose.yml`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docker-compose.yml)  
  *Gestiona `localspot-backend`, `localspot-postgres`, `localspot-frontend`, `localspot-sonarqube` y `localspot-pgadmin` sobre la red puente `localspot-net`.*
- **Variables de Entorno Centralizadas:** [`.env`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/.env)

---

## 5. Reportes de Calidad y Pruebas

### 5.1 Reporte de Rendimiento y Pruebas de Estrés (JMeter & k6)
- **Documento Técnico:** [`docs/PRUEBAS_CARGA_Y_ESTRES.md`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docs/PRUEBAS_CARGA_Y_ESTRES.md)
- **Plan de Pruebas JMeter:** [`performance/jmeter/localspot_9_escenarios_runner.jmx`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/localspot_9_escenarios_runner.jmx)
- **Dashboard Interactivo HTML de JMeter:**  
  👉 [`performance/jmeter/report-html/index.html`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/report-html/index.html)
- **Lanzador Interactivo Windows:** [`performance/jmeter/run-jmeter.bat`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/jmeter/run-jmeter.bat)
- **Archivos de Métricas k6:**
  - [`performance/results/load-summary.json`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/results/load-summary.json) (Carga 100 VUs: $p_{95} = 88.6\text{ ms}$, error $0\%$).
  - [`performance/results/validate-220vus-summary.json`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/results/validate-220vus-summary.json) (Validación sostenida 220 VUs: $p_{95} = 67.5\text{ ms}$, error $0\%$, $344\text{ req/s}$).
  - [`performance/results/stress-summary.json`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/performance/results/stress-summary.json) (Estrés hasta 450 VUs).

### 5.2 Reporte de Calidad de Código y Deuda Técnica (SonarQube)
- **Documento Técnico:** [`docs/SONARQUBE_ANALISIS.md`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docs/SONARQUBE_ANALISIS.md)
- **Panel Web de SonarQube:** [http://127.0.0.1:9000/dashboard?id=LocalSpot_Backend](http://127.0.0.1:9000/dashboard?id=LocalSpot_Backend)
- **Métricas Oficiales:** Quality Gate `PASSED (OK)`, 0 Bugs, 0 Smells, 0 min de deuda técnica, 90.0% de cobertura.

### 5.3 Reporte de Pruebas Funcionales (Postman)
- **Documento Técnico:** [`docs/PRUEBAS_FUNCIONALES_POSTMAN.md`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docs/PRUEBAS_FUNCIONALES_POSTMAN.md)
- **Métricas:** 36 endpoints evaluados, respuestas JSON reales, códigos HTTP y validación de seguridad.
