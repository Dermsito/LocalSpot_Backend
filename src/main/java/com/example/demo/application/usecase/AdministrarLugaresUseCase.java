package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CategoriaNoEncontradaException;
import com.example.demo.domain.exception.LugarNoEncontradoException;
import com.example.demo.domain.exception.VerificacionInvalidaException;
import com.example.demo.domain.model.EstadoVerificacion;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Pagina;
import com.example.demo.domain.model.Paginacion;
import com.example.demo.domain.port.AuditoriaPort;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.domain.port.FotoLugarRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * RF-10: CRUD de lugares por administrador + verificar/rechazar.
 * Valida precios, actualiza fecha_actualizacion y registra auditoria.
 */
public class AdministrarLugaresUseCase {

    private final LugarRepositoryPort lugarRepositoryPort;
    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final FotoLugarRepositoryPort fotoLugarRepositoryPort;
    private final AuditoriaPort auditoriaPort;

    public AdministrarLugaresUseCase(LugarRepositoryPort lugarRepositoryPort,
                                      CategoriaRepositoryPort categoriaRepositoryPort,
                                      FotoLugarRepositoryPort fotoLugarRepositoryPort,
                                      AuditoriaPort auditoriaPort) {
        this.lugarRepositoryPort = lugarRepositoryPort;
        this.categoriaRepositoryPort = categoriaRepositoryPort;
        this.fotoLugarRepositoryPort = fotoLugarRepositoryPort;
        this.auditoriaPort = auditoriaPort;
    }

    public Pagina<Lugar> listar(Paginacion paginacion) {
        return lugarRepositoryPort.buscarConFiltros(null, null, null, null, null,
                null, null, null, paginacion);
    }

    public Lugar crear(Long idAdmin, Lugar datos) {
        validarCategoria(datos.getIdCategoria());
        validarPrecios(datos.getPrecioMin(), datos.getPrecioMax());

        Lugar lugar = new Lugar();
        lugar.setIdCategoria(datos.getIdCategoria());
        lugar.setIdPropietario(null); // Admin crea lugares sin propietario
        lugar.setNombre(datos.getNombre());
        lugar.setDescripcion(datos.getDescripcion());
        lugar.setDireccion(datos.getDireccion());
        lugar.setLatitud(datos.getLatitud());
        lugar.setLongitud(datos.getLongitud());
        lugar.setPrecioMin(datos.getPrecioMin());
        lugar.setPrecioMax(datos.getPrecioMax());
        lugar.setEstadoVerificacion(EstadoVerificacion.VERIFICADO);
        lugar.setFechaRegistro(LocalDateTime.now());
        lugar.setFechaActualizacion(LocalDateTime.now());

        Lugar guardado = lugarRepositoryPort.save(lugar);
        auditoriaPort.registrar(idAdmin, "LUGAR", guardado.getIdLugar(), "CREATE", null,
                "nombre=" + guardado.getNombre());
        return guardado;
    }

    public Lugar actualizar(Long idAdmin, Long idLugar, Lugar datos) {
        Lugar existente = lugarRepositoryPort.findById(idLugar)
                .orElseThrow(() -> new LugarNoEncontradoException("Lugar no encontrado con id: " + idLugar));

        if (datos.getIdCategoria() != null) {
            validarCategoria(datos.getIdCategoria());
            existente.setIdCategoria(datos.getIdCategoria());
        }
        if (datos.getNombre() != null) existente.setNombre(datos.getNombre());
        if (datos.getDescripcion() != null) existente.setDescripcion(datos.getDescripcion());
        if (datos.getDireccion() != null) existente.setDireccion(datos.getDireccion());
        if (datos.getLatitud() != null) existente.setLatitud(datos.getLatitud());
        if (datos.getLongitud() != null) existente.setLongitud(datos.getLongitud());

        BigDecimal nuevoMin = datos.getPrecioMin() != null ? datos.getPrecioMin() : existente.getPrecioMin();
        BigDecimal nuevoMax = datos.getPrecioMax() != null ? datos.getPrecioMax() : existente.getPrecioMax();
        validarPrecios(nuevoMin, nuevoMax);
        existente.setPrecioMin(nuevoMin);
        existente.setPrecioMax(nuevoMax);

        existente.setFechaActualizacion(LocalDateTime.now());
        Lugar guardado = lugarRepositoryPort.save(existente);
        auditoriaPort.registrar(idAdmin, "LUGAR", idLugar, "UPDATE", null,
                "nombre=" + guardado.getNombre());
        return guardado;
    }

    public void eliminar(Long idAdmin, Long idLugar) {
        if (!lugarRepositoryPort.findById(idLugar).isPresent()) {
            throw new LugarNoEncontradoException("Lugar no encontrado con id: " + idLugar);
        }
        // Eliminar fotos primero para evitar huerfanos
        fotoLugarRepositoryPort.deleteByLugarId(idLugar);
        lugarRepositoryPort.deleteById(idLugar);
        auditoriaPort.registrar(idAdmin, "LUGAR", idLugar, "DELETE", null, null);
    }

    public Lugar cambiarVerificacion(Long idAdmin, Long idLugar, EstadoVerificacion estado) {
        if (estado == null) {
            throw new VerificacionInvalidaException("Estado de verificacion invalido");
        }
        Lugar lugar = lugarRepositoryPort.findById(idLugar)
                .orElseThrow(() -> new LugarNoEncontradoException("Lugar no encontrado con id: " + idLugar));
        lugar.setEstadoVerificacion(estado);
        lugar.setFechaActualizacion(LocalDateTime.now());
        Lugar guardado = lugarRepositoryPort.save(lugar);
        auditoriaPort.registrar(idAdmin, "LUGAR", idLugar, "UPDATE",
                "estado=" + lugar.getEstadoVerificacion(), "estado=" + estado);
        return guardado;
    }

    private void validarCategoria(Long idCategoria) {
        if (!categoriaRepositoryPort.existsById(idCategoria)) {
            throw new CategoriaNoEncontradaException("Categoria no encontrada con id: " + idCategoria);
        }
    }

    private void validarPrecios(BigDecimal precioMin, BigDecimal precioMax) {
        if (precioMin != null && precioMin.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio minimo no puede ser negativo");
        }
        if (precioMax != null && precioMax.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("El precio maximo no puede ser negativo");
        }
        if (precioMin != null && precioMax != null && precioMin.compareTo(precioMax) > 0) {
            throw new IllegalArgumentException("El precio minimo no puede ser mayor al precio maximo");
        }
    }
}
