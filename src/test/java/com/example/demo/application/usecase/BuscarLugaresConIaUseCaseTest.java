package com.example.demo.application.usecase;

import com.example.demo.domain.model.EstadoVerificacion;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Pagina;
import com.example.demo.domain.port.AiSearchPort;
import com.example.demo.domain.port.LugarRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuscarLugaresConIaUseCaseTest {

    @Mock
    private LugarRepositoryPort lugarRepositoryPort;

    @Mock
    private AiSearchPort aiSearchPort;

    @InjectMocks
    private BuscarLugaresConIaUseCase useCase;

    @Test
    void ejecutar_consultaVaciaOBlanca_retornaListaVacia() {
        assertThat(useCase.ejecutar(null)).isEmpty();
        assertThat(useCase.ejecutar("  ")).isEmpty();
    }

    @Test
    void ejecutar_sinLugaresVerificados_retornaListaVacia() {
        when(lugarRepositoryPort.buscarConFiltros(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new Pagina<>(Collections.emptyList(), 0, 10, 0, 0));

        assertThat(useCase.ejecutar("cafeteria tranquila")).isEmpty();
    }

    @Test
    void ejecutar_conLugares_retornaRecomendaciones() {
        Lugar l1 = new Lugar();
        l1.setIdLugar(10L);
        l1.setNombre("Café A");

        when(lugarRepositoryPort.buscarConFiltros(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new Pagina<>(List.of(l1), 0, 10, 1, 1));

        when(aiSearchPort.recomendarLugares(eq("cafeteria tranquila"), anyList()))
                .thenReturn(List.of(new AiSearchPort.RecomendacionIaItem(10L, "Excelente ambiente")));

        List<BuscarLugaresConIaUseCase.LugarRecomendado> res = useCase.ejecutar("cafeteria tranquila");

        assertThat(res).hasSize(1);
        assertThat(res.get(0).lugar().getNombre()).isEqualTo("Café A");
        assertThat(res.get(0).motivo()).isEqualTo("Excelente ambiente");
    }
}
