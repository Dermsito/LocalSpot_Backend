package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CategoriaNoEncontradaException;
import com.example.demo.domain.exception.LugarNoEncontradoException;
import com.example.demo.domain.model.EstadoVerificacion;
import com.example.demo.domain.model.FotoLugar;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.domain.port.FotoLugarRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * RF-13: Registro de establecimientos por parte de propietarios.
 * El lugar queda PENDIENTE y asociado al id del propietario.
 * Tambien permite consultar el estado de sus lugares.
 */
public class GestionarEstablecimientosUseCase {

    private final LugarRepositoryPort lugarRepositoryPort;
    private final CategoriaRepositoryPort categoriaRepositoryPort;
    private final FotoLugarRepositoryPort fotoLugarRepositoryPort;

    public GestionarEstablecimientosUseCase(LugarRepositoryPort lugarRepositoryPort,
            CategoriaRepositoryPort categoriaRepositoryPort,
            FotoLugarRepositoryPort fotoLugarRepositoryPort) {
        this.lugarRepositoryPort = lugarRepositoryPort;
        this.categoriaRepositoryPort = categoriaRepositoryPort;
        this.fotoLugarRepositoryPort = fotoLugarRepositoryPort;
    }

    public Lugar registrar(Long idPropietario, Lugar datos, List<String> urlsFotos) {
        if (datos.getIdCategoria() == null || !categoriaRepositoryPort.existsById(datos.getIdCategoria())) {
            throw new CategoriaNoEncontradaException("Categoria no encontrada");
        }
        validarPrecios(datos.getPrecioMin(), datos.getPrecioMax());

        Lugar lugar = new Lugar();
        lugar.setIdCategoria(datos.getIdCategoria());
        lugar.setIdPropietario(idPropietario);
        lugar.setNombre(datos.getNombre());
        lugar.setDescripcion(datos.getDescripcion());
        lugar.setDireccion(datos.getDireccion());
        lugar.setLatitud(datos.getLatitud());
        lugar.setLongitud(datos.getLongitud());
        lugar.setPrecioMin(datos.getPrecioMin());
        lugar.setPrecioMax(datos.getPrecioMax());
        lugar.setEstadoVerificacion(EstadoVerificacion.PENDIENTE);
        lugar.setFechaRegistro(LocalDateTime.now());
        lugar.setFechaActualizacion(LocalDateTime.now());

        Lugar guardado = lugarRepositoryPort.save(lugar);

        if (urlsFotos != null && !urlsFotos.isEmpty()) {
            List<FotoLugar> fotos = new ArrayList<>();
            for (String url : urlsFotos) {
                FotoLugar foto = new FotoLugar();
                foto.setIdLugar(guardado.getIdLugar());
                foto.setUrlFoto(url);
                foto.setFechaSubida(LocalDateTime.now());
                fotos.add(foto);
            }
            fotoLugarRepositoryPort.saveAll(fotos);
        }

        return guardado;
    }

    public List<Lugar> consultarMisLugares(Long idPropietario) {
        return lugarRepositoryPort.findByPropietarioId(idPropietario);
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
