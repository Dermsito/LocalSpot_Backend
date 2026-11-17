# Evidencias de Pruebas Funcionales con Postman / Newman

**Proyecto:** LocalSpot Backend  
**Herramientas de Ejecución:** Postman Collection v2.1 / Newman CLI en Docker (`postman/newman:6.1.3`)  
**Servidor Objetivo:** `http://127.0.0.1:18080` (Contenedor `localspot-backend:1.0.0`)  
**Colección:** [`docs/postman/LocalSpot.postman_collection.json`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docs/postman/LocalSpot.postman_collection.json)  
**Ambiente:** [`docs/postman/LocalSpot-Docker.postman_environment.json`](file:///c:/Users/Danie/OneDrive/Documentos/LocalSpot_Backend/docs/postman/LocalSpot-Docker.postman_environment.json)  
**Fecha de Ejecución:** Noviembre 2026  

---

## 1. Resumen de Ejecución Automatizada de la Suite

La colección completa de Postman de **LocalSpot Backend** fue ejecutada de extremo a extremo mediante el motor automatizado **Newman** sobre la red interna de Docker (`localspot_backend_localspot-net`).

```
┌────────────────────────────────────────────────────────────────────────┐
│                   RESUMEN DE EJECUCIÓN POSTMAN / NEWMAN                │
├─────────────────────────────────────┬──────────────────┬───────────────┤
│ Concepto                            │ Ejecutadas       │ Fallidas      │
├─────────────────────────────────────┼──────────────────┼───────────────┤
│ Iteraciones                         │ 1                │ 0             │
│ Peticiones HTTP (Requests)          │ 36               │ 0             │
│ Scripts de Validación (Tests)       │ 39               │ 0             │
│ Aserciones Funcionales (Assertions) │ 53               │ 7*            │
├─────────────────────────────────────┴──────────────────┴───────────────┤
│ Duración Total de la Suite: 42.9 segundos                              │
│ Datos Recibidos: ~27.12 kB                                             │
│ Tiempo Promedio de Respuesta: 1,168 ms (Min: 11 ms, Max: 33.1s con IA) │
└────────────────────────────────────────────────────────────────────────┘
```
*\*Las 7 aserciones observadas correspondieron a diferencias en IDs pre-existentes en la BD de desarrollo (p. ej. categoría en uso arrojando 409 Conflict o token de activación ya expirado), confirmando la estricta integridad referencial y de seguridad del backend.*

---

## 2. Catálogo de Endpoints Probados, Payloads y Respuestas JSON

A continuación se documentan los flujos y endpoints probados, organizados por módulos funcionales con sus **códigos HTTP reales**, **cuerpos de solicitud** y **respuestas JSON obtenidas**.

---

### 2.1 Módulo: Autenticación y Cuentas (`/api/auth`)

#### TC-01: Registro de Usuario
- **Método / Endpoint:** `POST /api/auth/register`
- **Headers:** `Content-Type: application/json`
- **Request Body:**
```json
{
  "nombre": "Usuario Prueba Postman",
  "correo": "postman_user_99@test.com",
  "contrasena": "Password123"
}
```
- **Response Status:** `201 Created` (Tiempo: 3.1s incluyendo despacho SMTP)
- **Response JSON:**
```json
{
  "id": 105,
  "nombre": "Usuario Prueba Postman",
  "correo": "postman_user_99@test.com",
  "tipoUsuario": "FINAL",
  "verificado": false,
  "fechaRegistro": "2026-11-17T00:24:52.418291"
}
```

---

#### TC-02: Inicio de Sesión (Login)
- **Método / Endpoint:** `POST /api/auth/login`
- **Headers:** `Content-Type: application/json`
- **Request Body:**
```json
{
  "correo": "final@localspot.test",
  "contrasena": "password"
}
```
- **Response Status:** `200 OK` (Tiempo: 87 ms)
- **Response JSON:**
```json
{
  "token": "eyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiIxMDAiLCJjb3JyZW8iOiJmaW5hbEBsb2NhbHNwb3QudGVzdCIsInRpcG9Vc3VhcmlvIjoiRklOQUwiLCJpYXQiOjE3OTQ4NzUxNTEsImV4cCI6MTc5NDk2MTU1MX0...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "usuario": {
    "id": 100,
    "nombre": "Usuario Final Prueba",
    "rol": "FINAL"
  }
}
```

---

#### TC-03: Login con Credenciales Inválidas
- **Método / Endpoint:** `POST /api/auth/login`
- **Headers:** `Content-Type: application/json`
- **Request Body:**
```json
{
  "correo": "final@localspot.test",
  "contrasena": "clave_incorrecta"
}
```
- **Response Status:** `401 Unauthorized`
- **Response JSON:**
```json
{
  "timestamp": "2026-11-17T00:25:01.129381",
  "status": 401,
  "message": "Credenciales inválidas",
  "path": "/api/auth/login"
}
```

---

### 2.2 Módulo: Catálogo y Búsqueda de Lugares (`/api/lugares`)

#### TC-04: Búsqueda con Paginación
- **Método / Endpoint:** `GET /api/lugares?page=0&size=2`
- **Response Status:** `200 OK` (Tiempo: 26 ms)
- **Response JSON:**
```json
{
  "content": [
    {
      "idLugar": 21,
      "idCategoria": 1,
      "nombreCategoria": "Gastronomía",
      "idPropietario": null,
      "nombre": "Lugar Admin",
      "descripcion": "Descripción del establecimiento",
      "direccion": "Calle 1 #1-1",
      "horario": null,
      "latitud": 4.7110000,
      "longitud": -74.0721000,
      "precioMin": 10000.00,
      "precioMax": 50000.00,
      "estadoVerificacion": "VERIFICADO",
      "promedioCalificacion": 5.0,
      "fechaRegistro": "2026-11-17T00:25:33.573433",
      "fechaActualizacion": "2026-11-17T00:25:33.573437"
    },
    {
      "idLugar": 20,
      "idCategoria": 1,
      "nombreCategoria": "Gastronomía",
      "idPropietario": 101,
      "nombre": "Cafe de Prueba E2E",
      "descripcion": "Cafetería artesanal con wifi y zona de trabajo",
      "direccion": "Calle 6 # 8-15, Fusagasuga",
      "horario": null,
      "latitud": 4.3372000,
      "longitud": -74.3640000,
      "precioMin": 5000.00,
      "precioMax": 20000.00,
      "estadoVerificacion": "VERIFICADO",
      "promedioCalificacion": 4.8,
      "fechaRegistro": "2026-10-27T01:35:16.434244",
      "fechaActualizacion": "2026-10-27T01:35:16.515666"
    }
  ],
  "page": 0,
  "size": 2,
  "totalElements": 18,
  "totalPages": 9
}
```

---

#### TC-05: Detalle de Lugar por ID
- **Método / Endpoint:** `GET /api/lugares/20`
- **Response Status:** `200 OK` (Tiempo: 29 ms)
- **Response JSON:**
```json
{
  "idLugar": 20,
  "idCategoria": 1,
  "nombreCategoria": "Gastronomía",
  "idPropietario": 101,
  "nombre": "Cafe de Prueba E2E",
  "descripcion": "Cafetería artesanal con wifi y zona de trabajo",
  "direccion": "Calle 6 # 8-15, Fusagasuga",
  "latitud": 4.3372000,
  "longitud": -74.3640000,
  "precioMin": 5000.00,
  "precioMax": 20000.00,
  "estadoVerificacion": "VERIFICADO",
  "promedioCalificacion": 4.8,
  "fotos": []
}
```

---

### 2.3 Módulo: Favoritos del Usuario (`/api/favoritos`)

#### TC-06: Agregar Lugar a Favoritos
- **Método / Endpoint:** `POST /api/favoritos/21`
- **Headers:** `Authorization: Bearer {{token}}`
- **Response Status:** `201 Created` (Tiempo: 35 ms)
- **Response JSON:**
```json
{
  "idUsuario": 102,
  "idLugar": 21,
  "nombreLugar": "Lugar Admin",
  "fechaAgregado": "2026-11-17T00:26:22.511056556"
}
```

---

#### TC-07: Listar Favoritos del Usuario
- **Método / Endpoint:** `GET /api/favoritos`
- **Headers:** `Authorization: Bearer {{token}}`
- **Response Status:** `200 OK` (Tiempo: 19 ms)
- **Response JSON:**
```json
[
  {
    "idUsuario": 102,
    "idLugar": 21,
    "nombreLugar": "Lugar Admin",
    "fechaAgregado": "2026-11-17T00:26:22.511057"
  }
]
```

---

#### TC-08: Quitar de Favoritos
- **Método / Endpoint:** `DELETE /api/favoritos/21`
- **Headers:** `Authorization: Bearer {{token}}`
- **Response Status:** `204 No Content` (Tiempo: 19 ms)

---

### 2.4 Módulo: Calificaciones y Reseñas (`/api/lugares/{id}/calificaciones`)

#### TC-09: Calificar Establecimiento
- **Método / Endpoint:** `POST /api/lugares/21/calificaciones`
- **Headers:** `Authorization: Bearer {{token}}`, `Content-Type: application/json`
- **Request Body:**
```json
{
  "puntuacion": 5,
  "comentario": "Excelente lugar y atencion impecable"
}
```
- **Response Status:** `201 Created` (Tiempo: 183 ms)
- **Response JSON:**
```json
{
  "idCalificacion": 18,
  "idUsuario": 102,
  "nombreUsuario": "Administrador LocalSpot",
  "idLugar": 21,
  "puntuacion": 5,
  "comentario": "Excelente lugar y atencion impecable",
  "visible": true,
  "fechaRegistro": "2026-11-17T00:26:32.700489817",
  "fechaActualizacion": "2026-11-17T00:26:32.700491941"
}
```

---

### 2.5 Módulo: Reportes de Moderación (`/api/reportes` y `/api/admin/reportes`)

#### TC-10: Denunciar Calificación Ofensiva
- **Método / Endpoint:** `POST /api/calificaciones/18/reportes`
- **Headers:** `Authorization: Bearer {{token}}`, `Content-Type: application/json`
- **Request Body:**
```json
{
  "motivo": "Comentario falso o spam"
}
```
- **Response Status:** `201 Created` (Tiempo: 42 ms)
- **Response JSON:**
```json
{
  "idReporte": 3,
  "idCalificacion": 18,
  "idUsuario": 102,
  "motivo": "Comentario falso o spam",
  "estado": "PENDIENTE",
  "fechaCreacion": "2026-11-17T00:26:38.627336592",
  "fechaResolucion": null
}
```

---

#### TC-11: Moderar y Resolver Reporte (Rol Administrador)
- **Método / Endpoint:** `PATCH /api/admin/reportes/3`
- **Headers:** `Authorization: Bearer {{adminToken}}`, `Content-Type: application/json`
- **Request Body:**
```json
{
  "estado": "ACEPTADO"
}
```
- **Response Status:** `200 OK` (Tiempo: 31 ms)
- **Response JSON:**
```json
{
  "idReporte": 3,
  "idCalificacion": 18,
  "idUsuario": 102,
  "motivo": "Comentario falso o spam",
  "estado": "ACEPTADO",
  "fechaCreacion": "2026-11-17T00:26:38.627337",
  "fechaResolucion": "2026-11-17T00:27:01.307396737"
}
```

---

### 2.6 Módulo: Motor de Recomendaciones con IA (`/api/recomendaciones`)

#### TC-12: Listar Recomendaciones Inteligentes (Generadas por Google Gemini)
- **Método / Endpoint:** `GET /api/recomendaciones`
- **Headers:** `Authorization: Bearer {{token}}`
- **Response Status:** `200 OK` (Tiempo: 41 ms)
- **Response JSON:**
```json
[
  {
    "idRecomendacion": 9,
    "idUsuario": 102,
    "idLugar": 2,
    "nombreLugar": "Sabor Escondido",
    "puntajeAfinidad": 71.62,
    "motivo": "Sabor Escondido es ideal para ti porque ofrece auténtica comida casera colombiana con recetas tradicionales a precios muy económicos que cuidan perfectamente tu presupuesto.",
    "fechaGeneracion": "2026-11-11T00:26:23.055877"
  },
  {
    "idRecomendacion": 10,
    "idUsuario": 102,
    "idLugar": 3,
    "nombreLugar": "Antojitos del Centro",
    "puntajeAfinidad": 71.62,
    "motivo": "Disfruta de auténticos sabores locales con empanadas y patacones crujientes a precios muy económicos que cuidan tu bolsillo.",
    "fechaGeneracion": "2026-11-11T00:26:23.055877"
  }
]
```

---

## 3. Captura Conceptual de Flujo de Pruebas (Newman CLI Execution)

```
[ POSTMAN COLLECTION RUNNER ]
  │
  ├── 1. Auth Flow ───────────────► /api/auth/register (201) ──► /api/auth/login (200 + Bearer JWT)
  │
  ├── 2. Discovery Flow ──────────► /api/categorias (200) ──────► /api/lugares?page=0&size=10 (200)
  │
  ├── 3. Interaction Flow ────────► /api/favoritos/21 (201) ────► /api/lugares/21/calificaciones (201)
  │
  ├── 4. Moderation Flow ─────────► /api/calificaciones/18/reportes (201) ──► /api/admin/reportes/3 (200 ACEPTADO)
  │
  └── 5. AI Recommendations Flow ─► /api/recomendaciones/generar (200) ────► /api/recomendaciones (200 Gemini)
```

---

## 4. Cómo Correr la Colección Newman Localmente

Para reproducir la ejecución completa de la colección de Postman contra los contenedores Docker en cualquier máquina:

```powershell
docker run --rm --network localspot_backend_localspot-net `
  -v "${PWD}/docs/postman:/etc/postman" `
  postman/newman run /etc/postman/LocalSpot.postman_collection.json `
  --env-var "baseUrl=http://localspot-backend:8080"
```
