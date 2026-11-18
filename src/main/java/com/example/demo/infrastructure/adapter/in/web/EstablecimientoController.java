package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.GestionarEstablecimientosUseCase;
import com.example.demo.domain.exception.LugarNoEncontradoException;
import com.example.demo.domain.exception.OperacionNoAutorizadaException;
import com.example.demo.domain.model.Archivo;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.port.LugarRepositoryPort;
import com.example.demo.domain.port.StoragePort;
import com.example.demo.infrastructure.adapter.in.web.dto.LugarRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.LugarResponse;
import com.example.demo.infrastructure.config.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/establecimientos")
@Tag(name = "Establecimientos", description = "Propietario: registro y actualizacion de establecimientos")
public class EstablecimientoController {

    private final GestionarEstablecimientosUseCase gestionarEstablecimientosUseCase;
    private final StoragePort storagePort;
    private final LugarRepositoryPort lugarRepositoryPort;

    public EstablecimientoController(GestionarEstablecimientosUseCase gestionarEstablecimientosUseCase,
            StoragePort storagePort,
            LugarRepositoryPort lugarRepositoryPort) {
        this.gestionarEstablecimientosUseCase = gestionarEstablecimientosUseCase;
        this.storagePort = storagePort;
        this.lugarRepositoryPort = lugarRepositoryPort;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Registrar establecimiento", description = "Registra un establecimiento (PENDIENTE) con fotos. Solo PROPIETARIO.")
    public ResponseEntity<LugarResponse> registrar(
            @RequestParam("nombre") String nombre,
            @RequestParam("idCategoria") Long idCategoria,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "direccion", required = false) String direccion,
            @RequestParam(value = "horario", required = false) String horario,
            @RequestParam(value = "latitud", required = false) BigDecimal latitud,
            @RequestParam(value = "longitud", required = false) BigDecimal longitud,
            @RequestParam(value = "precioMin", required = false) BigDecimal precioMin,
            @RequestParam(value = "precioMax", required = false) BigDecimal precioMax,
            @RequestParam(value = "fotos", required = false) List<MultipartFile> fotos) throws IOException {

        Long idPropietario = SecurityUtils.getCurrentUsuarioId();

        if (latitud == null || longitud == null) {
            latitud = new BigDecimal("4.3372");
            longitud = new BigDecimal("-74.3640");
        }

        Lugar datos = new Lugar();
        datos.setNombre(nombre);
        datos.setIdCategoria(idCategoria);
        datos.setDescripcion(descripcion);
        datos.setDireccion(direccion);
        datos.setHorario(horario);
        datos.setLatitud(latitud);
        datos.setLongitud(longitud);
        datos.setPrecioMin(precioMin);
        datos.setPrecioMax(precioMax);

        List<String> urlsFotos = new ArrayList<>();
        if (fotos != null) {
            for (MultipartFile file : fotos) {
                if (!file.isEmpty()) {
                    Archivo archivo = new Archivo(
                            file.getOriginalFilename(),
                            file.getBytes(),
                            file.getContentType());
                    String url = storagePort.store(archivo);
                    urlsFotos.add(url);
                }
            }
        }

        Lugar lugar = gestionarEstablecimientosUseCase.registrar(idPropietario, datos, urlsFotos);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(lugar));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar mi establecimiento", description = "Actualiza datos y coordenadas de un lugar perteneciente al propietario.")
    public ResponseEntity<LugarResponse> actualizarMiLugar(
            @PathVariable Long id,
            @Valid @RequestBody LugarRequest request) {
        Long idPropietario = SecurityUtils.getCurrentUsuarioId();
        Lugar existente = lugarRepositoryPort.findById(id)
                .orElseThrow(() -> new LugarNoEncontradoException("Lugar no encontrado con id: " + id));

        if (existente.getIdPropietario() == null || !existente.getIdPropietario().equals(idPropietario)) {
            throw new OperacionNoAutorizadaException("No tienes permiso para editar este establecimiento");
        }

        existente.setNombre(request.getNombre());
        existente.setDescripcion(request.getDescripcion());
        existente.setDireccion(request.getDireccion());
        existente.setHorario(request.getHorario());
        if (request.getIdCategoria() != null) {
            existente.setIdCategoria(request.getIdCategoria());
        }
        if (request.getLatitud() != null && request.getLongitud() != null) {
            existente.setLatitud(request.getLatitud());
            existente.setLongitud(request.getLongitud());
        }
        if (request.getPrecioMin() != null) {
            existente.setPrecioMin(request.getPrecioMin());
        }
        if (request.getPrecioMax() != null) {
            existente.setPrecioMax(request.getPrecioMax());
        }

        Lugar guardado = lugarRepositoryPort.save(existente);
        return ResponseEntity.ok(toResponse(guardado));
    }

    @GetMapping("/mis-lugares")
    @Operation(summary = "Mis lugares", description = "Lista los lugares registrados por el propietario autenticado.")
    public ResponseEntity<List<LugarResponse>> misLugares() {
        Long idPropietario = SecurityUtils.getCurrentUsuarioId();
        List<Lugar> lugares = gestionarEstablecimientosUseCase.consultarMisLugares(idPropietario);
        List<LugarResponse> response = lugares.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
        return r;
    }
}
