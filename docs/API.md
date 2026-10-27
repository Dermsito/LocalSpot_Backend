# LocalSpot API Reference

Base URL: `http://127.0.0.1:18080`

---

## Autenticacion

Todos los endpoints (excepto `/api/auth/**`) requieren el header:

```
Authorization: Bearer <token>
```

### POST /api/auth/register

Registra un nuevo usuario final. La cuenta queda **sin verificar** y se envia un
correo con el enlace de verificacion; no se puede iniciar sesion hasta confirmarlo.

```json
{
  "nombre": "Juan Perez",
  "correo": "juan@example.com",
  "contrasena": "MiPassword123"
}
```

**Respuesta 201:**

```json
{
  "id": 1,
  "nombre": "Juan Perez",
  "correo": "juan@example.com",
  "rol": "final",
  "fechaRegistro": "2026-10-26T20:15:00"
}
```

### POST /api/auth/verificar-correo

Confirma la cuenta con el `token` recibido por correo y habilita el login.

```json
{
  "token": "8f14e45f-ceea-467f-a8f5-2f9b6f4b1e11"
}
```

**Respuesta 200:**

```json
{ "mensaje": "Correo verificado correctamente" }
```

**Errores:** `400` si el token no existe, ya fue usado o expiro.

### POST /api/auth/reenviar-verificacion

Genera un nuevo token (invalida los anteriores) y lo envia por correo.

```json
{
  "correo": "juan@example.com"
}
```

**Respuesta 200:** siempre el mismo mensaje, exista o no la cuenta:

```json
{ "mensaje": "Si el correo esta registrado y sin verificar, se envio un nuevo enlace de verificacion" }
```

### POST /api/auth/login

Inicia sesion.

```json
{
  "correo": "final@localspot.test",
  "contrasena": "password"
}
```

**Respuesta 200:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "usuario": {
    "id": 101,
    "nombre": "Usuario Final",
    "rol": "FINAL"
  }
}
```

**Errores:** `401` credenciales invalidas, `403` si el correo aun no esta verificado.

### POST /api/auth/logout

Cierra sesion (requiere token).

---

## Integraciones externas

- **Google Maps Geocoding** (`MAPS_API_KEY`): convierte direcciones en coordenadas.
  Si la clave esta vacia se usa un stub que solo entiende `"latitud,longitud"` y, en
  cualquier otro caso, devuelve el centro configurado (`MAPS_DEFAULT_LAT` /
  `MAPS_DEFAULT_LON`, por defecto Fusagasuga).
- **Google Gemini** (`AI_API_KEY`): genera el motivo de cada recomendacion.
  Sin clave se usa un stub local. En ambos casos hay CircuitBreaker + Retry y un
  motivo de respaldo si el servicio falla.
- **Correo SMTP** (`MAIL_HOST` / `MAIL_PORT`): envio de la verificacion. En desarrollo
  apunta a Mailpit (`http://127.0.0.1:18025`).

---

## Lugares

### GET /api/lugares

Busca lugares verificados.

**Query params:**

| Param       | Tipo   | Descripcion                          |
|-------------|--------|--------------------------------------|
| `q`         | string | Texto libre en nombre o descripcion  |
| `categoriaId`| long  | Filtro por categoria                 |
| `ubicacion` | string | Nombre de lugar para geocodificar    |
| `precioMin` | decimal| Precio minimo                        |
| `precioMax` | decimal| Precio maximo                        |
| `page`      | int    | Pagina (default 0)                   |
| `size`      | int    | Tamano de pagina (default 10)        |

**Respuesta 200:**

```json
{
  "content": [
    {
      "idLugar": 1,
      "nombre": "Pizzeria El Rincon",
      "nombreCategoria": "Restaurantes",
      "direccion": "Calle 6 # 8-15, Fusagasuga",
      "latitud": 4.3372,
      "longitud": -74.3640,
      "precioMin": 20000,
      "precioMax": 80000,
      "promedioCalificacion": 4.5
    }
  ],
  "page": 0,
  "size": 10,
  "totalElements": 1,
  "totalPages": 1
}
```

### GET /api/lugares/{id}

Obtiene el detalle de un lugar, incluyendo fotos, promedio de calificaciones y comentarios visibles.

---

## Categorias

### GET /api/categorias

Lista todas las categorias (publico).

---

## Calificaciones

### POST /api/lugares/{idLugar}/calificaciones

Crea una calificacion para un lugar (requiere FINAL).

```json
{
  "puntaje": 5,
  "comentario": "Excelente servicio"
}
```

**Errores:** `409` si el usuario ya califico el lugar.

### PUT /api/lugares/{idLugar}/calificaciones

Actualiza la calificacion del usuario para un lugar.

---

## Favoritos

### GET /api/favoritos

Lista los favoritos del usuario autenticado.

### POST /api/favoritos/{lugarId}

Agrega un lugar a favoritos. **Error:** `409` si ya esta en favoritos.

### DELETE /api/favoritos/{lugarId}

Elimina un lugar de favoritos.

---

## Preferencias

### GET /api/preferencias

Lista las preferencias del usuario autenticado.

### POST /api/preferencias

Crea una preferencia de categoria.

```json
{
  "idCategoria": 1,
  "presupuestoMax": 100000
}
```

**Errores:** `409` si ya existe una preferencia para la categoria.

### PUT /api/preferencias/{id}

Actualiza el presupuesto de una preferencia.

### DELETE /api/preferencias/{id}

Elimina una preferencia.

---

## Recomendaciones

### POST /api/recomendaciones/generar

Genera recomendaciones de lugares basadas en las preferencias del usuario.

### GET /api/recomendaciones

Lista las recomendaciones generadas.

---

## Reportes

### POST /api/calificaciones/{idCalificacion}/reportes

Reporta una calificacion (requiere FINAL).

```json
{
  "motivo": "Contenido inapropiado"
}
```

**Errores:** `409` si el usuario ya reporto la calificacion.

---

## Admin (rol ADMIN)

### GET /api/admin/lugares

Lista todos los lugares con filtros y paginacion.

### GET /api/admin/categorias

Lista todas las categorias.

### POST /api/admin/categorias

Crea una categoria.

### PUT /api/admin/categorias/{id}

Actualiza una categoria.

### DELETE /api/admin/categorias/{id}

Elimina una categoria. **Error:** `409` si la categoria esta en uso.

### GET /api/admin/reportes

Lista los reportes pendientes.

### PUT /api/admin/reportes/{id}/estado

Actualiza el estado de un reporte (`PENDIENTE`, `RESUELTO`, `DESCARTADO`).

---

## Errores comunes

| Code | Significado                              |
|------|------------------------------------------|
| 400  | Solicitud invalida o error de validacion |
| 401  | Autenticacion requerida o credenciales invalidas |
| 403  | Acceso denegado (rol insuficiente)       |
| 404  | Recurso no encontrado                    |
| 409  | Conflicto (duplicado o en uso)           |
