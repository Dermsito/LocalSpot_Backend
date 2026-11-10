package com.example.demo.application.usecase;

import com.example.demo.domain.exception.RecomendacionSinPreferenciasException;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.model.Pagina;
import com.example.demo.domain.model.Preferencia;
import com.example.demo.domain.model.Recomendacion;
import com.example.demo.domain.port.AiRecommendationPort;
import com.example.demo.domain.port.CalificacionRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;
import com.example.demo.domain.port.PreferenciaRepositoryPort;
import com.example.demo.domain.port.RecomendacionRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerarRecomendacionesUseCaseTest {

    @Mock
    private LugarRepositoryPort lugarRepositoryPort;

    @Mock
    private PreferenciaRepositoryPort preferenciaRepositoryPort;

    @Mock
    private RecomendacionRepositoryPort recomendacionRepositoryPort;

    @Mock
    private CalificacionRepositoryPort calificacionRepositoryPort;

    @Mock
    private AiRecommendationPort aiRecommendationPort;

    @InjectMocks
    private GenerarRecomendacionesUseCase useCase;

    @Test
    void ejecutar_sinPreferencias_debeLanzarExcepcion() {
        when(preferenciaRepositoryPort.findByIdUsuario(1L)).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> useCase.ejecutar(1L))
                .isInstanceOf(RecomendacionSinPreferenciasException.class);
    }

    @Test
    void ejecutar_conPreferencias_nuevaRecomendacion() {
        Preferencia pref = new Preferencia();
        pref.setIdCategoria(10L);
        pref.setPresupuestoMax(BigDecimal.valueOf(100));
        when(preferenciaRepositoryPort.findByIdUsuario(1L)).thenReturn(List.of(pref));

        Lugar lugar = new Lugar();
        lugar.setIdLugar(5L);
        lugar.setIdCategoria(10L);
        lugar.setNombre("Lugar Oculto");
        when(lugarRepositoryPort.buscarConFiltros(any(), eq(10L), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new Pagina<>(List.of(lugar), 0, 10, 1, 1));

        when(calificacionRepositoryPort.calcularPromedioByLugarId(5L)).thenReturn(4.5);
        when(calificacionRepositoryPort.findByLugarIdAndVisibleTrue(5L)).thenReturn(Collections.emptyList());
        when(aiRecommendationPort.generateMotivo(any(), any())).thenReturn("Motivo sugerido");
        when(recomendacionRepositoryPort.findByUsuarioIdAndLugarId(1L, 5L)).thenReturn(Optional.empty());
        when(recomendacionRepositoryPort.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        List<Recomendacion> resultado = useCase.ejecutar(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getIdLugar()).isEqualTo(5L);
        assertThat(resultado.get(0).getMotivo()).isEqualTo("Motivo sugerido");
    }

    @Test
    void ejecutar_conRecomendacionExistente_yFalloEnIa() {
        Preferencia pref = new Preferencia();
        pref.setIdCategoria(10L);
        when(preferenciaRepositoryPort.findByIdUsuario(1L)).thenReturn(List.of(pref));

        Lugar lugar = new Lugar();
        lugar.setIdLugar(5L);
        lugar.setIdCategoria(20L); // Categoria distinta para cubrir orElse
        when(lugarRepositoryPort.buscarConFiltros(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(new Pagina<>(List.of(lugar), 0, 10, 1, 1));

        when(calificacionRepositoryPort.calcularPromedioByLugarId(5L)).thenReturn(4.0);
        when(calificacionRepositoryPort.findByLugarIdAndVisibleTrue(5L)).thenReturn(Collections.emptyList());
        when(aiRecommendationPort.generateMotivo(any(), any())).thenThrow(new RuntimeException("AI error"));

        Recomendacion existente = new Recomendacion();
        existente.setIdRecomendacion(99L);
        when(recomendacionRepositoryPort.findByUsuarioIdAndLugarId(1L, 5L)).thenReturn(Optional.of(existente));
        when(recomendacionRepositoryPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        List<Recomendacion> resultado = useCase.ejecutar(1L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getMotivo()).contains("Lugar poco conocido");
    }
}
