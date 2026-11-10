package com.example.demo.application.usecase;

import com.example.demo.domain.exception.LugarNoEncontradoException;
import com.example.demo.domain.model.Calificacion;
import com.example.demo.domain.model.FotoLugar;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.port.CalificacionRepositoryPort;
import com.example.demo.domain.port.FotoLugarRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObtenerDetalleLugarUseCaseTest {

    @Mock
    private LugarRepositoryPort lugarRepositoryPort;

    @Mock
    private FotoLugarRepositoryPort fotoLugarRepositoryPort;

    @Mock
    private CalificacionRepositoryPort calificacionRepositoryPort;

    @InjectMocks
    private ObtenerDetalleLugarUseCase useCase;

    @Test
    void ejecutar_cuandoLugarNoExiste_debeLanzarExcepcion() {
        when(lugarRepositoryPort.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.ejecutar(1L))
                .isInstanceOf(LugarNoEncontradoException.class);
    }

    @Test
    void ejecutar_cuandoLugarExiste_debeRetornarDetalleCompleto() {
        Lugar lugar = new Lugar();
        lugar.setIdLugar(1L);
        lugar.setNombre("Café Central");

        FotoLugar foto = new FotoLugar();
        foto.setUrlFoto("http://foto.jpg");

        Calificacion cal = new Calificacion();
        cal.setPuntuacion((short) 5);

        when(lugarRepositoryPort.findById(1L)).thenReturn(Optional.of(lugar));
        when(fotoLugarRepositoryPort.findByLugarId(1L)).thenReturn(List.of(foto));
        when(calificacionRepositoryPort.findByLugarIdAndVisibleTrue(1L)).thenReturn(List.of(cal));
        when(calificacionRepositoryPort.calcularPromedioByLugarId(1L)).thenReturn(4.8);

        ObtenerDetalleLugarUseCase.DetalleLugar detalle = useCase.ejecutar(1L);

        assertThat(detalle.lugar().getNombre()).isEqualTo("Café Central");
        assertThat(detalle.fotos()).hasSize(1);
        assertThat(detalle.calificaciones()).hasSize(1);
        assertThat(detalle.promedio()).isEqualTo(4.8);
    }
}
