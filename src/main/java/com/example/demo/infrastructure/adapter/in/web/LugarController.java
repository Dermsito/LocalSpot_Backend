package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.BuscarLugaresUseCase;
import com.example.demo.application.usecase.ObtenerDetalleLugarUseCase;
import com.example.demo.domain.model.Calificacion;
import com.example.demo.domain.model.Coordenadas;
import com.example.demo.domain.model.FotoLugar;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Pagina;
import com.example.demo.domain.model.Paginacion;
import com.example.demo.domain.port.CalificacionRepositoryPort;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.domain.port.GeocodingPort;
import com.example.demo.infrastructure.adapter.in.web.dto.CalificacionResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.FotoResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.LugarDetalleResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.LugarResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.PaginaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lugares")
@Tag(name = "Lugares", description = "Busqueda y detalle de lugares")
public class LugarController {

    private final BuscarLugaresUseCase buscarLugaresUseCase;
    private final ObtenerDetalleLugarUseCase obtenerDetalleLugarUseCase;
    private final GeocodingPort geocodingPort;
    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final CalificacionRepositoryPort calificacionRepositoryPort;

    public LugarController(BuscarLugaresUseCase buscarLugaresUseCase,
            ObtenerDetalleLugarUseCase obtenerDetalleLugarUseCase,
            GeocodingPort geocodingPort,
            CategoriaRepositoryPort categoriaRepositoryPort,
            CalificacionRepositoryPort calificacionRepositoryPort) {
        this.buscarLugaresUseCase = buscarLugaresUseCase;
        this.obtenerDetalleLugarUseCase = obtenerDetalleLugarUseCase;
        this.geocodingPort = geocodingPort;
        this.categoriaRepositoryPort = categoriaRepositoryPort;
        this.calificacionRepositoryPort = calificacionRepositoryPort;
    }

    @GetMapping
    @Operation(summary = "Buscar lugares", description = "Busqueda por texto, categoria, ubicacion y rango de precio. Solo lugares VERIFICADOS.")
    public ResponseEntity<PaginaResponse<LugarResponse>> buscar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long categoriaId,
            @RequestParam(required = false) String ubicacion,
            @RequestParam(required = false) BigDecimal precioMin,
            @RequestParam(required = false) BigDecimal precioMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        BigDecimal latitud = null;
        BigDecimal longitud = null;
        BigDecimal radioKm = null;

        if (ubicacion != null && !ubicacion.isBlank()) {
            Coordenadas coords = geocodingPort.geocode(ubicacion);
            latitud = coords.latitud();
            longitud = coords.longitud();
            radioKm = coords.radioKm();
        }

        Paginacion paginacion = new Paginacion(page, size);
        Pagina<Lugar> resultado = buscarLugaresUseCase.ejecutar(
                q, categoriaId, latitud, longitud, radioKm, precioMin, precioMax, paginacion);

        List<LugarResponse> content = resultado.content().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        PaginaResponse<LugarResponse> response = new PaginaResponse<>(
                content, resultado.page(), resultado.size(),
                resultado.totalElements(), resultado.totalPages());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalle de lugar", description = "Obtiene datos, fotos, mapa, promedio y comentarios visibles.")
    public ResponseEntity<LugarDetalleResponse> detalle(@PathVariable Long id) {
        ObtenerDetalleLugarUseCase.DetalleLugar detalle = obtenerDetalleLugarUseCase.ejecutar(id);
        LugarDetalleResponse response = toDetalleResponse(detalle);
        return ResponseEntity.ok(response);
    }

    private LugarResponse toResponse(Lugar lugar) {
        LugarResponse r = new LugarResponse();
        r.setIdLugar(lugar.getIdLugar());
        r.setIdCategoria(lugar.getIdCategoria());
        r.setIdPropietario(lugar.getIdPropietario());
        r.setNombre(lugar.getNombre());
        r.setDescripcion(lugar.getDescripcion());
        r.setDireccion(lugar.getDireccion());
        r.setHorario(lugar.getHorario());
        r.setLatitud(lugar.getLatitud());
        r.setLongitud(lugar.getLongitud());
        r.setPrecioMin(lugar.getPrecioMin());
        r.setPrecioMax(lugar.getPrecioMax());
        r.setEstadoVerificacion(lugar.getEstadoVerificacion() != null ? lugar.getEstadoVerificacion().name() : null);
        r.setFechaRegistro(lugar.getFechaRegistro());
        r.setFechaActualizacion(lugar.getFechaActualizacion());
        // Nombre de categoria
        categoriaRepositoryPort.findById(lugar.getIdCategoria())
                .ifPresent(c -> r.setNombreCategoria(c.getNombreCategoria()));
        // Promedio de calificaciones
        r.setPromedioCalificacion(calificacionRepositoryPort.calcularPromedioByLugarId(lugar.getIdLugar()));
        return r;
    }

    private LugarDetalleResponse toDetalleResponse(ObtenerDetalleLugarUseCase.DetalleLugar detalle) {
        Lugar lugar = detalle.lugar();
        LugarDetalleResponse r = new LugarDetalleResponse();
        r.setIdLugar(lugar.getIdLugar());
        r.setIdCategoria(lugar.getIdCategoria());
        r.setNombre(lugar.getNombre());
        r.setDescripcion(lugar.getDescripcion());
        r.setDireccion(lugar.getDireccion());
        r.setHorario(lugar.getHorario());
        r.setLatitud(lugar.getLatitud());
        r.setLongitud(lugar.getLongitud());
        r.setPrecioMin(lugar.getPrecioMin());
        r.setPrecioMax(lugar.getPrecioMax());
        r.setEstadoVerificacion(lugar.getEstadoVerificacion() != null ? lugar.getEstadoVerificacion().name() : null);
        r.setPromedioCalificacion(detalle.promedio());
        r.setFechaRegistro(lugar.getFechaRegistro());

        categoriaRepositoryPort.findById(lugar.getIdCategoria())
                .ifPresent(c -> r.setNombreCategoria(c.getNombreCategoria()));

        List<FotoResponse> fotos = detalle.fotos().stream()
                .map(f -> new FotoResponse(f.getIdFotoLugar(), f.getIdLugar(), f.getUrlFoto(), f.getFechaSubida()))
                .collect(Collectors.toList());
        r.setFotos(fotos);

        List<CalificacionResponse> califs = detalle.calificaciones().stream()
                .map(this::toCalificacionResponse)
                .collect(Collectors.toList());
        r.setCalificaciones(califs);

        return r;
    }

    private CalificacionResponse toCalificacionResponse(Calificacion c) {
        CalificacionResponse r = new CalificacionResponse();
        r.setIdCalificacion(c.getIdCalificacion());
        r.setIdUsuario(c.getIdUsuario());
        r.setIdLugar(c.getIdLugar());
        r.setPuntuacion(c.getPuntuacion());
        r.setComentario(c.getComentario());
        r.setVisible(c.getVisible());
        r.setFechaRegistro(c.getFechaRegistro());
        r.setFechaActualizacion(c.getFechaActualizacion());
        return r;
    }
}
