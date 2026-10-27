# Diagrama ERD - LocalSpot

```mermaid
erDiagram
    USUARIOS {
        BIGINT id_usuario PK
        VARCHAR nombre
        VARCHAR correo UK
        VARCHAR contrasena_hash
        VARCHAR tipo_usuario
        TIMESTAMP fecha_registro
    }

    CATEGORIAS {
        BIGINT id_categoria PK
        VARCHAR nombre_categoria UK
    }

    PREFERENCIAS {
        BIGINT id_preferencia PK
        BIGINT id_usuario FK
        BIGINT id_categoria FK
        NUMERIC presupuesto_max
    }

    LUGAR {
        BIGINT id_lugar PK
        BIGINT id_categoria FK
        BIGINT id_propietario FK
        VARCHAR nombre
        TEXT descripcion
        VARCHAR direccion
        NUMERIC latitud
        NUMERIC longitud
        NUMERIC precio_min
        NUMERIC precio_max
        VARCHAR estado_verificacion
        TIMESTAMP fecha_registro
        TIMESTAMP fecha_actualizacion
    }

    FOTO_LUGAR {
        BIGINT id_foto_lugar PK
        BIGINT id_lugar FK
        VARCHAR url_foto
        TIMESTAMP fecha_subida
    }

    FAVORITO {
        BIGINT id_usuario PK,FK
        BIGINT id_lugar PK,FK
        TIMESTAMP fecha_agregado
    }

    CALIFICACION {
        BIGINT id_calificacion PK
        BIGINT id_usuario FK
        BIGINT id_lugar FK
        SMALLINT puntuacion
        TEXT comentario
        BOOLEAN visible
        TIMESTAMP fecha_registro
        TIMESTAMP fecha_actualizacion
    }

    RECOMENDACION {
        BIGINT id_recomendacion PK
        BIGINT id_usuario FK
        BIGINT id_lugar FK
        NUMERIC puntaje_afinidad
        VARCHAR motivo
        TIMESTAMP fecha_generacion
    }

    REPORTE {
        BIGINT id_reporte PK
        BIGINT id_calificacion FK
        BIGINT id_usuario FK
        VARCHAR motivo
        VARCHAR estado
        TIMESTAMP fecha_reporte
    }

    USUARIOS ||--o{ PREFERENCIAS : "tiene"
    CATEGORIAS ||--o{ PREFERENCIAS : "asociada"
    CATEGORIAS ||--o{ LUGAR : "clasifica"
    USUARIOS ||--o{ LUGAR : "propieta"
    LUGAR ||--o{ FOTO_LUGAR : "tiene"
    USUARIOS ||--o{ FAVORITO : "marca"
    LUGAR ||--o{ FAVORITO : "favorito"
    USUARIOS ||--o{ CALIFICACION : "califica"
    LUGAR ||--o{ CALIFICACION : "recibe"
    USUARIOS ||--o{ RECOMENDACION : "recibe"
    LUGAR ||--o{ RECOMENDACION : "recomendado"
    CALIFICACION ||--o{ REPORTE : "reporta"
    USUARIOS ||--o{ REPORTE : "genera"
```

## Notas

- **usuarios.tipo_usuario**: `ADMINISTRADOR`, `FINAL` o `PROPIETARIO`
- **lugar.estado_verificacion**: `PENDIENTE`, `VERIFICADO` o `RECHAZADO`
- **favorito** tiene PK compuesta `(id_usuario, id_lugar)`
- **calificacion** tiene restriccion UNIQUE `(id_usuario, id_lugar)`
- **preferencias** tiene restriccion UNIQUE `(id_usuario, id_categoria)`
