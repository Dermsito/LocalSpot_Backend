# LocalSpot Backend

Backend de la aplicacion LocalSpot construido con **Spring Boot 3.3.5**, **Java 17**, **PostgreSQL 16** y **Docker**.
Implementa una arquitectura hexagonal (puertos y adaptadores) con JWT para autenticacion y Flyway para migraciones de BD.

---

## Documentación Técnica

- **[INFORME TÉCNICO INTEGRAL](docs/INFORME_TECNICO_INTEGRAL.md)**: 📄 **Documento maestro consolidado** que reúne arquitectura hexagonal, principios SOLID, metodología 12 factores, SonarQube, Postman, JMeter, k6, telemetría en BD y la guía exacta de capturas para sustentación.
- **[Anexos Técnicos y Entregables](docs/ANEXOS.md)**: Repositorios Git, scripts SQL de verificación (`verificacion.sql`), colección Postman, archivos Docker y reportes de pruebas.
- **[Pruebas Funcionales con Postman / Newman](docs/PRUEBAS_FUNCIONALES_POSTMAN.md)**: 36 endpoints probados, respuestas JSON reales, códigos HTTP, flujo E2E y comandos para correr la colección.
- **[Reporte y Métricas SonarQube](docs/SONARQUBE_ANALISIS.md)**: Integración con SonarQube, Quality Gate (OK), 0 bugs/smells, deuda técnica (0 min) y cobertura JaCoCo (90.0%).
- **[Metodología 12-Factor App](docs/DOCE_FACTORES.md)**: Tabla detallada factor/implementación con evidencias de código y configuración.
- **[Principios SOLID en la Arquitectura](docs/PRINCIPIOS_SOLID.md)**: Explicación de los 5 principios SOLID, fragmentos de código, diagramas y verificación con ArchUnit.
- **[Especificación y Arquitectura Técnica](docs/ARQUITECTURA_TECNICA.md)**: Arquitectura hexagonal, puertos y adaptadores, resiliencia (Resilience4j), telemetría asíncrona en BD y seguridad.
- **[Pruebas de Carga y Estrés (JMeter & k6)](docs/PRUEBAS_CARGA_Y_ESTRES.md)**: 9 escenarios obligatorios E2E, métricas reales, latencias percentil $p_{90}$/$p_{95}$ y dashboard HTML.
- **[Contrato de API REST](docs/API.md)**: Documentación de endpoints y payloads.

---

## Requisitos

- Docker y Docker Compose
- Java 17 (solo para desarrollo fuera de Docker)
- Maven (o usar el wrapper `mvnw.cmd`)

---

## Arranque rapido (Docker)

```powershell
docker compose up -d --build
```

Servicios levantados:

| Servicio   | URL                          | Descripcion                        |
|------------|------------------------------|------------------------------------|
| Backend    | http://127.0.0.1:18080       | API REST (Swagger en `/swagger-ui.html`) |
| PostgreSQL | 127.0.0.1:15432              | Base de datos                      |
| pgAdmin    | http://127.0.0.1:15050       | Panel de administracion de BD      |
| Mailpit    | http://127.0.0.1:18025       | Bandeja de correo de desarrollo    |
| Frontend   | http://127.0.0.1:3000        | Aplicacion web                     |

> Todos los puertos estan restringidos a `127.0.0.1` por seguridad.

### Credenciales por defecto

| Tipo          | Email                       | Password     |
|---------------|-----------------------------|--------------|
| Administrador | admin@localspot.test        | Admin123456  |
| Usuario final | final@localspot.test        | password     |
| Propietario   | propietario@localspot.test  | password     |

### pgAdmin

- Email: `admin@localspot.test`
- Password: `Admin123456`
- Host del servidor PostgreSQL dentro de Docker: `postgres` (puerto 5432)

---

## Desarrollo local

### Compilar

```powershell
.\mvnw.cmd clean compile
```

### Ejecutar tests unitarios

```powershell
.\mvnw.cmd test
```

> Los tests de integracion (`*IntegrationTest`) se excluyen por defecto ya que requieren Docker daemon para Testcontainers.

### Reporte de cobertura (JaCoCo)

```powershell
.\mvnw.cmd verify
```

El reporte HTML se genera en `target/site/jacoco/index.html`.
Se exige una cobertura minima del **70%** en los paquetes `domain` y `application`.

---

## Variables de entorno

| Variable        | Descripcion                          | Requerida |
|-----------------|--------------------------------------|-----------|
| `AI_API_KEY`    | API key de Google Gemini. Si esta vacia se usa el stub de IA | No |
| `AI_API_URL`    | URL base de Gemini                   | No (default `https://generativelanguage.googleapis.com`) |
| `AI_MODEL`      | Modelo de Gemini                     | No (default `gemini-3.5-flash-lite`) |
| `AI_MAX_MOTIVOS` | Maximo de motivos generados por IA por generacion de recomendaciones | No (default `12`) |
| `AI_MAX_LUGARES_PROMPT` | Maximo de lugares incluidos en el prompt de la busqueda inteligente | No (default `40`) |
| `MAPS_API_KEY`  | API key de Google Geocoding. Si esta vacia se usa el stub | No |
| `MAPS_API_URL`  | URL del endpoint de Geocoding        | No |
| `MAPS_DEFAULT_LAT` / `MAPS_DEFAULT_LON` | Centro por defecto cuando no se puede geocodificar (Fusagasuga) | No |
| `MAIL_HOST` / `MAIL_PORT` | Servidor SMTP para la verificacion de correo | No (default `mailpit:1025`) |
| `MAIL_USERNAME` / `MAIL_PASSWORD` | Credenciales SMTP (vacio en Mailpit) | No |
| `MAIL_FROM`     | Remitente de los correos             | No (default `no-reply@localspot.test`) |
| `APP_BASE_URL`  | URL del frontend usada en el enlace de verificacion | No (default `http://localhost:3000`) |
| `VERIFICACION_EXPIRACION_MINUTOS` | Vigencia del token de verificacion | No (default `1440`) |
| `UPLOAD_DIR`    | Directorio de subida de archivos     | No (default `./uploads`) |
| `ADMIN_PASSWORD`| Password del usuario administrador   | No (default `Admin123456`) |

---

## Verificacion de correo

El registro (`POST /api/auth/register`) crea la cuenta **sin verificar** y envia un
correo con el enlace de verificacion. El login responde `403` hasta que el usuario
confirme con `POST /api/auth/verificar-correo`.

En desarrollo los correos se capturan en Mailpit: abre http://127.0.0.1:18025,
copia el token del mensaje y envialo a `/api/auth/verificar-correo`.
Para reenviar el enlace existe `POST /api/auth/reenviar-verificacion`.

---

## Estructura del proyecto

```
src/main/java/com/example/demo/
├── domain/            # Entidades, excepciones, puertos (interfaces)
├── application/       # Casos de uso (logica de negocio)
└── infrastructure/    # Adaptadores: web (controllers), persistence, config
```

---

## Documentacion adicional

- [API Reference](docs/API.md)
- [Diagrama ERD](docs/sql/diagrama.md)
- [Script de verificacion de BD](docs/sql/verificacion.sql)
- [Coleccion Postman](docs/postman/LocalSpot.postman_collection.json)
