package com.example.demo.domain.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DomainExceptionsCoverageTest {

    @Test
    void testAllDomainExceptions() {
        assertThat(new CalificacionDuplicadaException("msg")).hasMessage("msg");
        assertThat(new CalificacionNoEncontradaException("msg")).hasMessage("msg");
        assertThat(new CategoriaEnUsoException("msg")).hasMessage("msg");
        assertThat(new CategoriaNoEncontradaException("msg")).hasMessage("msg");
        assertThat(new CorreoDuplicadoException("msg")).hasMessage("msg");
        assertThat(new CorreoNoVerificadoException("msg")).hasMessage("msg");
        assertThat(new CredencialesInvalidasException("msg")).hasMessage("msg");
        assertThat(new EstadoReporteInvalidoException("msg")).hasMessage("msg");
        assertThat(new FavoritoDuplicadoException("msg")).hasMessage("msg");
        assertThat(new GeocodificacionFallidaException("msg")).hasMessage("msg");
        assertThat(new LugarNoEncontradoException("msg")).hasMessage("msg");
        assertThat(new OperacionNoAutorizadaException("msg")).hasMessage("msg");
        assertThat(new PreferenciaDuplicadaException("msg")).hasMessage("msg");
        assertThat(new PreferenciaNoEncontradaException("msg")).hasMessage("msg");
        assertThat(new PresupuestoInvalidoException("msg")).hasMessage("msg");
        assertThat(new RecomendacionSinPreferenciasException("msg")).hasMessage("msg");
        assertThat(new ReporteDuplicadoException("msg")).hasMessage("msg");
        assertThat(new ReporteNoEncontradoException("msg")).hasMessage("msg");
        assertThat(new TokenRecuperacionInvalidoException("msg")).hasMessage("msg");
        assertThat(new TokenVerificacionInvalidoException("msg")).hasMessage("msg");
        assertThat(new UsuarioNoEncontradoException("msg")).hasMessage("msg");
        assertThat(new VerificacionInvalidaException("msg")).hasMessage("msg");
    }
}
