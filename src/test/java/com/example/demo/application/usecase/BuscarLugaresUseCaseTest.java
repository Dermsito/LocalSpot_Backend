package com.example.demo.application.usecase;

import com.example.demo.domain.model.EstadoVerificacion;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Pagina;
import com.example.demo.domain.model.Paginacion;
import com.example.demo.domain.port.LugarRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarLugaresUseCaseTest {

    @Mock
    private LugarRepositoryPort lugarRepositoryPort;

    @InjectMocks
    private BuscarLugaresUseCase buscarLugaresUseCase;

    private Lugar lugarVerificado;

    @BeforeEach
    void setUp() {
        lugarVerificado = new Lugar();
        lugarVerificado.setIdLugar(1L);
        lugarVerificado.setNombre("Pizzeria Test");
        lugarVerificado.setEstadoVerificacion(EstadoVerificacion.VERIFICADO);
    }

    @Test
    void buscar_debeRetornarSoloVerificados() {
        Pagina<Lugar> pagina = new Pagina<>(List.of(lugarVerificado), 0, 10, 1L, 1);
        when(lugarRepositoryPort.buscarConFiltros(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(pagina);

        Paginacion paginacion = new Paginacion(0, 10);
        Pagina<Lugar> resultado = buscarLugaresUseCase.ejecutar(
                "pizza", 1L, null, null, null, null, null, paginacion);

        assertThat(resultado.content()).hasSize(1);
        assertThat(resultado.content().get(0).getNombre()).isEqualTo("Pizzeria Test");
    }

    @Test
    void buscar_conRangoInvalido_debeLanzarExcepcion() {
        BigDecimal min = new BigDecimal("50000");
        BigDecimal max = new BigDecimal("10000");

        Paginacion paginacion = new Paginacion(0, 10);
        assertThatThrownBy(() -> buscarLugaresUseCase.ejecutar(
                null, null, null, null, null, min, max, paginacion))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
