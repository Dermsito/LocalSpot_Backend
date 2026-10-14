package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.EstadoVerificacion;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Pagina;
import com.example.demo.domain.model.Paginacion;
import com.example.demo.domain.port.LugarRepositoryPort;
import com.example.demo.infrastructure.adapter.out.persistence.entity.LugarJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.LugarJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class LugarRepositoryAdapter implements LugarRepositoryPort {

    private final LugarJpaRepository lugarJpaRepository;
    private final JdbcTemplate jdbcTemplate;

    public LugarRepositoryAdapter(LugarJpaRepository lugarJpaRepository, JdbcTemplate jdbcTemplate) {
        this.lugarJpaRepository = lugarJpaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<Lugar> findById(Long idLugar) {
        return lugarJpaRepository.findById(idLugar).map(this::toDomain);
    }

    @Override
    public Lugar save(Lugar lugar) {
        LugarJpaEntity entity = toEntity(lugar);
        LugarJpaEntity saved = lugarJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public void deleteById(Long idLugar) {
        lugarJpaRepository.deleteById(idLugar);
    }

    @Override
    public boolean existsByCategoriaId(Long idCategoria) {
        return lugarJpaRepository.existsByIdCategoria(idCategoria);
    }

    @Override
    public List<Lugar> findByPropietarioId(Long idPropietario) {
        return lugarJpaRepository.findByIdPropietario(idPropietario,
                org.springframework.data.domain.PageRequest.of(0, 1000))
                .getContent().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Pagina<Lugar> buscarConFiltros(String q, Long categoriaId, BigDecimal latitud,
            BigDecimal longitud, BigDecimal radioKm,
            BigDecimal precioMin, BigDecimal precioMax,
            EstadoVerificacion estadoVerificacion, Paginacion paginacion) {
        String estadoStr = estadoVerificacion != null ? estadoVerificacion.name() : null;
        int limit = paginacion.size();
        int offset = paginacion.page() * paginacion.size();

        String sql = """
                SELECT l.id_lugar FROM lugar l
                WHERE (?::text IS NULL OR LOWER(l.nombre) LIKE LOWER(CONCAT('%', ?::text, '%'))
                       OR LOWER(l.descripcion) LIKE LOWER(CONCAT('%', ?::text, '%')))
                  AND (?::bigint IS NULL OR l.id_categoria = ?::bigint)
                  AND (?::text IS NULL OR l.estado_verificacion = ?::text)
                  AND (?::numeric IS NULL OR l.precio_max >= ?::numeric)
                  AND (?::numeric IS NULL OR l.precio_min <= ?::numeric)
                  AND (?::numeric IS NULL OR ?::numeric IS NULL OR ?::numeric IS NULL
                       OR (6371 * ACOS(LEAST(1.0, GREATEST(-1.0,
                           COS(RADIANS(?::numeric)) * COS(RADIANS(l.latitud))
                           * COS(RADIANS(l.longitud) - RADIANS(?::numeric))
                           + SIN(RADIANS(?::numeric)) * SIN(RADIANS(l.latitud))
                       ))) <= ?::numeric))
                ORDER BY l.fecha_registro DESC
                LIMIT ?::int OFFSET ?::int
                """;

        List<Long> ids = jdbcTemplate.queryForList(sql, Long.class,
                q, q, q,
                categoriaId, categoriaId,
                estadoStr, estadoStr,
                precioMin, precioMin,
                precioMax, precioMax,
                latitud, longitud, radioKm,
                latitud, longitud, latitud, radioKm,
                limit, offset);

        List<LugarJpaEntity> entities = ids.isEmpty() ? List.of()
                : lugarJpaRepository.findAllById(ids);

        java.util.Map<Long, LugarJpaEntity> byId = entities.stream()
                .collect(Collectors.toMap(LugarJpaEntity::getIdLugar, e -> e));
        List<Lugar> content = ids.stream()
                .map(byId::get)
                .filter(java.util.Objects::nonNull)
                .map(this::toDomain)
                .collect(Collectors.toList());

        String countSql = """
                SELECT COUNT(*) FROM lugar l
                WHERE (?::text IS NULL OR LOWER(l.nombre) LIKE LOWER(CONCAT('%', ?::text, '%'))
                       OR LOWER(l.descripcion) LIKE LOWER(CONCAT('%', ?::text, '%')))
                  AND (?::bigint IS NULL OR l.id_categoria = ?::bigint)
                  AND (?::text IS NULL OR l.estado_verificacion = ?::text)
                  AND (?::numeric IS NULL OR l.precio_max >= ?::numeric)
                  AND (?::numeric IS NULL OR l.precio_min <= ?::numeric)
                  AND (?::numeric IS NULL OR ?::numeric IS NULL OR ?::numeric IS NULL
                       OR (6371 * ACOS(LEAST(1.0, GREATEST(-1.0,
                           COS(RADIANS(?::numeric)) * COS(RADIANS(l.latitud))
                           * COS(RADIANS(l.longitud) - RADIANS(?::numeric))
                           + SIN(RADIANS(?::numeric)) * SIN(RADIANS(l.latitud))
                       ))) <= ?::numeric))
                """;

        Long total = jdbcTemplate.queryForObject(countSql, Long.class,
                q, q, q,
                categoriaId, categoriaId,
                estadoStr, estadoStr,
                precioMin, precioMin,
                precioMax, precioMax,
                latitud, longitud, radioKm,
                latitud, longitud, latitud, radioKm);

        int totalPages = (int) Math.ceil((double) (total != null ? total : 0) / limit);
        return new Pagina<>(content, paginacion.page(), paginacion.size(), total != null ? total : 0, totalPages);
    }

    private Lugar toDomain(LugarJpaEntity entity) {
        Lugar lugar = new Lugar();
        lugar.setIdLugar(entity.getIdLugar());
        lugar.setIdCategoria(entity.getIdCategoria());
        lugar.setIdPropietario(entity.getIdPropietario());
        lugar.setNombre(entity.getNombre());
        lugar.setDescripcion(entity.getDescripcion());
        lugar.setDireccion(entity.getDireccion());
        lugar.setHorario(entity.getHorario());
        lugar.setLatitud(entity.getLatitud());
        lugar.setLongitud(entity.getLongitud());
        lugar.setPrecioMin(entity.getPrecioMin());
        lugar.setPrecioMax(entity.getPrecioMax());
        if (entity.getEstadoVerificacion() != null) {
            lugar.setEstadoVerificacion(EstadoVerificacion.valueOf(entity.getEstadoVerificacion().name()));
        }
        lugar.setFechaRegistro(entity.getFechaRegistro());
        lugar.setFechaActualizacion(entity.getFechaActualizacion());
        return lugar;
    }

    private LugarJpaEntity toEntity(Lugar lugar) {
        LugarJpaEntity entity = new LugarJpaEntity();
        entity.setIdLugar(lugar.getIdLugar());
        entity.setIdCategoria(lugar.getIdCategoria());
        entity.setIdPropietario(lugar.getIdPropietario());
        entity.setNombre(lugar.getNombre());
        entity.setDescripcion(lugar.getDescripcion());
        entity.setDireccion(lugar.getDireccion());
        entity.setHorario(lugar.getHorario());
        entity.setLatitud(lugar.getLatitud());
        entity.setLongitud(lugar.getLongitud());
        entity.setPrecioMin(lugar.getPrecioMin());
        entity.setPrecioMax(lugar.getPrecioMax());
        if (lugar.getEstadoVerificacion() != null) {
            entity.setEstadoVerificacion(
                    LugarJpaEntity.EstadoVerificacionJpa.valueOf(lugar.getEstadoVerificacion().name()));
        }
        entity.setFechaRegistro(lugar.getFechaRegistro());
        entity.setFechaActualizacion(lugar.getFechaActualizacion());
        return entity;
    }
}
