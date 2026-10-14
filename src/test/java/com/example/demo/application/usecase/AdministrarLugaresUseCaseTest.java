package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CategoriaNoEncontradaException;
import com.example.demo.domain.exception.LugarNoEncontradoException;
import com.example.demo.domain.exception.VerificacionInvalidaException;
import com.example.demo.domain.model.EstadoVerificacion;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.port.AuditoriaPort;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.domain.port.FotoLugarRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdministrarLugaresUseCaseTest {

    @Mock
    private LugarRepositoryPort lugarRepositoryPort;
    @Mock
    private CategoriaRepositoryPort categoriaRepositoryPort;
    @Mock
    private FotoLugarRepositoryPort fotoLugarRepositoryPort;
    @Mock
    private AuditoriaPort auditoriaPort;

    @InjectMocks
    private AdministrarLugaresUseCase administrarLugaresUseCase;

    private Lugar datosLugar() {
        Lugar l = new Lugar();
        l.setNombre("Test");
        l.setIdCategoria(1L);
        l.setPrecioMin(BigDecimal.ZERO);
        l.setPrecioMax(new BigDecimal("10000"));
        return l;
    }

    @Test
    void crear_cuandoCategoriaValida_debeCrearVerificado() {
        when(categoriaRepositoryPort.existsById(1L)).thenReturn(true);
        when(lugarRepositoryPort.save(any(Lugar.class))).thenAnswer(inv -> {
            Lugar l = inv.getArgument(0);
            l.setIdLugar(1L);
            return l;
        });

        Lugar creado = administrarLugaresUseCase.crear(1L, datosLugar());

        assertThat(creado.getEstadoVerificacion()).isEqualTo(EstadoVerificacion.VERIFICADO);
        assertThat(creado.getIdPropietario()).isNull();
        verify(auditoriaPort, times(1)).registrar(anyLong(), eq("LUGAR"), any(), eq("CREATE"), any(), any());
    }

    @Test
    void crear_cuandoCategoriaNoExiste_debeLanzarExcepcion() {
        when(categoriaRepositoryPort.existsById(99L)).thenReturn(false);
        Lugar l = datosLugar();
        l.setIdCategoria(99L);

        assertThatThrownBy(() -> administrarLugaresUseCase.crear(1L, l))
                .isInstanceOf(CategoriaNoEncontradaException.class);
    }

    @Test
    void actualizar_cuandoExiste_debeActualizar() {
        Lugar existente = datosLugar();
        existente.setIdLugar(1L);
        when(lugarRepositoryPort.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepositoryPort.existsById(1L)).thenReturn(true);
        when(lugarRepositoryPort.save(any(Lugar.class))).thenAnswer(inv -> inv.getArgument(0));

        Lugar actualizado = administrarLugaresUseCase.actualizar(1L, 1L, datosLugar());

        assertThat(actualizado.getNombre()).isEqualTo("Test");
    }

    @Test
    void eliminar_cuandoExiste_debeEliminarFotosYLugar() {
        when(lugarRepositoryPort.findById(1L)).thenReturn(Optional.of(new Lugar()));

        administrarLugaresUseCase.eliminar(1L, 1L);

        verify(fotoLugarRepositoryPort, times(1)).deleteByLugarId(1L);
        verify(lugarRepositoryPort, times(1)).deleteById(1L);
    }

    @Test
    void cambiarVerificacion_cuandoEstadoNulo_debeLanzarExcepcion() {
        assertThatThrownBy(() -> administrarLugaresUseCase.cambiarVerificacion(1L, 1L, null))
                .isInstanceOf(VerificacionInvalidaException.class);
    }

    @Test
    void cambiarVerificacion_cuandoLugarNoExiste_debeLanzarExcepcion() {
        when(lugarRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> administrarLugaresUseCase.cambiarVerificacion(1L, 99L, EstadoVerificacion.VERIFICADO))
                .isInstanceOf(LugarNoEncontradoException.class);
    }
}
