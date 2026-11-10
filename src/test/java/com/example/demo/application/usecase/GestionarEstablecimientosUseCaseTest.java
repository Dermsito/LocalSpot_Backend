package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CategoriaNoEncontradaException;
import com.example.demo.domain.model.EstadoVerificacion;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.domain.port.FotoLugarRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionarEstablecimientosUseCaseTest {

    @Mock
    private LugarRepositoryPort lugarRepositoryPort;

    @Mock
    private CategoriaRepositoryPort categoriaRepositoryPort;

    @Mock
    private FotoLugarRepositoryPort fotoLugarRepositoryPort;

    @InjectMocks
    private GestionarEstablecimientosUseCase useCase;

    @Test
    void registrar_categoriaNoExiste_debeLanzarExcepcion() {
        Lugar datos = new Lugar();
        datos.setIdCategoria(99L);
        when(categoriaRepositoryPort.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> useCase.registrar(1L, datos, Collections.emptyList()))
                .isInstanceOf(CategoriaNoEncontradaException.class);
    }

    @Test
    void registrar_preciosInvalidos_debeLanzarExcepcion() {
        Lugar datos = new Lugar();
        datos.setIdCategoria(1L);
        when(categoriaRepositoryPort.existsById(1L)).thenReturn(true);

        datos.setPrecioMin(BigDecimal.valueOf(-1));
        assertThatThrownBy(() -> useCase.registrar(1L, datos, null))
                .isInstanceOf(IllegalArgumentException.class);

        datos.setPrecioMin(BigDecimal.valueOf(100));
        datos.setPrecioMax(BigDecimal.valueOf(-5));
        assertThatThrownBy(() -> useCase.registrar(1L, datos, null))
                .isInstanceOf(IllegalArgumentException.class);

        datos.setPrecioMax(BigDecimal.valueOf(50));
        assertThatThrownBy(() -> useCase.registrar(1L, datos, null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void registrar_exitoso_conFotos() {
        Lugar datos = new Lugar();
        datos.setIdCategoria(1L);
        datos.setNombre("Café");
        datos.setPrecioMin(BigDecimal.valueOf(10));
        datos.setPrecioMax(BigDecimal.valueOf(20));

        when(categoriaRepositoryPort.existsById(1L)).thenReturn(true);
        when(lugarRepositoryPort.save(any())).thenAnswer(inv -> {
            Lugar l = inv.getArgument(0);
            l.setIdLugar(50L);
            return l;
        });

        Lugar resultado = useCase.registrar(2L, datos, List.of("http://foto1.png", "http://foto2.png"));

        assertThat(resultado.getIdLugar()).isEqualTo(50L);
        assertThat(resultado.getEstadoVerificacion()).isEqualTo(EstadoVerificacion.PENDIENTE);
        verify(fotoLugarRepositoryPort, times(1)).saveAll(anyList());
    }

    @Test
    void consultarMisLugares_debeRetornarLista() {
        Lugar l = new Lugar();
        l.setIdLugar(1L);
        when(lugarRepositoryPort.findByPropietarioId(5L)).thenReturn(List.of(l));

        List<Lugar> resultado = useCase.consultarMisLugares(5L);
        assertThat(resultado).hasSize(1);
    }
}
