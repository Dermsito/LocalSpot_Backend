package com.example.demo.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class DomainModelCoverageTest {

    @Test
    void testArchivo() {
        byte[] bytes = new byte[]{1, 2, 3};
        Archivo a = new Archivo("foto.png", bytes, "image/png");
        assertThat(a.nombreOriginal()).isEqualTo("foto.png");
        assertThat(a.contenido()).isEqualTo(bytes);
        assertThat(a.contentType()).isEqualTo("image/png");
        assertThat(a.toString()).contains("foto.png");
        assertThat(a).isEqualTo(new Archivo("foto.png", bytes, "image/png"));
        assertThat(a.hashCode()).isNotZero();
    }

    @Test
    void testAuditoria() {
        LocalDateTime now = LocalDateTime.now();
        Auditoria aud2 = new Auditoria();
        aud2.setIdAuditoria(5L);
        aud2.setIdUsuario(6L);
        aud2.setEntidad("E");
        aud2.setIdRegistro(7L);
        aud2.setAccion("A");
        aud2.setDatosAnteriores("V1");
        aud2.setDatosNuevos("V2");
        aud2.setFechaAccion(now);

        assertThat(aud2.getIdAuditoria()).isEqualTo(5L);
        assertThat(aud2.getIdUsuario()).isEqualTo(6L);
        assertThat(aud2.getEntidad()).isEqualTo("E");
        assertThat(aud2.getIdRegistro()).isEqualTo(7L);
        assertThat(aud2.getAccion()).isEqualTo("A");
        assertThat(aud2.getDatosAnteriores()).isEqualTo("V1");
        assertThat(aud2.getDatosNuevos()).isEqualTo("V2");
        assertThat(aud2.getFechaAccion()).isEqualTo(now);
    }

    @Test
    void testCalificacion() {
        LocalDateTime now = LocalDateTime.now();
        Calificacion c2 = new Calificacion();
        c2.setIdCalificacion(1L);
        c2.setIdLugar(2L);
        c2.setIdUsuario(3L);
        c2.setPuntuacion((short) 4);
        c2.setComentario("Ok");
        c2.setVisible(false);
        c2.setFechaRegistro(now);
        c2.setFechaActualizacion(now);

        assertThat(c2.getIdCalificacion()).isEqualTo(1L);
        assertThat(c2.getIdLugar()).isEqualTo(2L);
        assertThat(c2.getIdUsuario()).isEqualTo(3L);
        assertThat(c2.getPuntuacion()).isEqualTo((short) 4);
        assertThat(c2.getComentario()).isEqualTo("Ok");
        assertThat(c2.getVisible()).isFalse();
        assertThat(c2.getFechaRegistro()).isEqualTo(now);
        assertThat(c2.getFechaActualizacion()).isEqualTo(now);
    }

    @Test
    void testCoordenadas() {
        Coordenadas coord = new Coordenadas(BigDecimal.valueOf(4.5), BigDecimal.valueOf(-74.1), BigDecimal.valueOf(5.0));
        assertThat(coord.latitud()).isEqualTo(BigDecimal.valueOf(4.5));
        assertThat(coord.longitud()).isEqualTo(BigDecimal.valueOf(-74.1));
        assertThat(coord.radioKm()).isEqualTo(BigDecimal.valueOf(5.0));
    }

    @Test
    void testEmailCorreo() {
        EmailCorreo email = new EmailCorreo("dest@test.com", "Asunto", "cuerpo");
        assertThat(email.destinatario()).isEqualTo("dest@test.com");
        assertThat(email.asunto()).isEqualTo("Asunto");
        assertThat(email.cuerpo()).isEqualTo("cuerpo");
    }

    @Test
    void testReporte() {
        LocalDateTime now = LocalDateTime.now();
        Reporte r2 = new Reporte();
        r2.setIdReporte(10L);
        r2.setIdCalificacion(20L);
        r2.setIdUsuario(30L);
        r2.setMotivo("Otro");
        r2.setEstado(EstadoReporte.ACEPTADO);
        r2.setFechaCreacion(now);
        r2.setFechaResolucion(now);

        assertThat(r2.getIdReporte()).isEqualTo(10L);
        assertThat(r2.getIdCalificacion()).isEqualTo(20L);
        assertThat(r2.getIdUsuario()).isEqualTo(30L);
        assertThat(r2.getMotivo()).isEqualTo("Otro");
        assertThat(r2.getEstado()).isEqualTo(EstadoReporte.ACEPTADO);
        assertThat(r2.getFechaCreacion()).isEqualTo(now);
        assertThat(r2.getFechaResolucion()).isEqualTo(now);
    }

    @Test
    void testFotoLugar() {
        LocalDateTime now = LocalDateTime.now();
        FotoLugar f2 = new FotoLugar();
        f2.setIdFotoLugar(1L);
        f2.setIdLugar(2L);
        f2.setUrlFoto("http://url");
        f2.setFechaSubida(now);

        assertThat(f2.getIdFotoLugar()).isEqualTo(1L);
        assertThat(f2.getIdLugar()).isEqualTo(2L);
        assertThat(f2.getUrlFoto()).isEqualTo("http://url");
        assertThat(f2.getFechaSubida()).isEqualTo(now);
    }

    @Test
    void testFavorito() {
        LocalDateTime now = LocalDateTime.now();
        Favorito fav = new Favorito(1L, 2L, now);
        assertThat(fav.getIdUsuario()).isEqualTo(1L);
        assertThat(fav.getIdLugar()).isEqualTo(2L);
        assertThat(fav.getFechaAgregado()).isEqualTo(now);

        Favorito fav2 = new Favorito();
        fav2.setIdUsuario(2L);
        fav2.setIdLugar(3L);
        fav2.setFechaAgregado(now);
        assertThat(fav2.getIdUsuario()).isEqualTo(2L);
    }

    @Test
    void testRecomendacion() {
        LocalDateTime now = LocalDateTime.now();
        Recomendacion rec2 = new Recomendacion();
        rec2.setIdRecomendacion(1L);
        rec2.setIdUsuario(2L);
        rec2.setIdLugar(3L);
        rec2.setPuntajeAfinidad(BigDecimal.TEN);
        rec2.setMotivo("M");
        rec2.setFechaGeneracion(now);

        assertThat(rec2.getIdRecomendacion()).isEqualTo(1L);
        assertThat(rec2.getIdUsuario()).isEqualTo(2L);
        assertThat(rec2.getIdLugar()).isEqualTo(3L);
        assertThat(rec2.getPuntajeAfinidad()).isEqualTo(BigDecimal.TEN);
        assertThat(rec2.getMotivo()).isEqualTo("M");
        assertThat(rec2.getFechaGeneracion()).isEqualTo(now);
    }

    @Test
    void testTokenRecuperacionContrasena() {
        LocalDateTime now = LocalDateTime.now();
        TokenRecuperacionContrasena t = new TokenRecuperacionContrasena(1L, 2L, "tok", now, now.plusHours(1), false);
        assertThat(t.getIdToken()).isEqualTo(1L);
        assertThat(t.getIdUsuario()).isEqualTo(2L);
        assertThat(t.getToken()).isEqualTo("tok");
        assertThat(t.getFechaExpiracion()).isEqualTo(now.plusHours(1));
        assertThat(t.isUsado()).isFalse();
        assertThat(t.getFechaCreacion()).isEqualTo(now);
        assertThat(t.estaVigente(now)).isTrue();
        assertThat(t.estaVigente(now.plusHours(2))).isFalse();

        t.setUsado(true);
        assertThat(t.estaVigente(now)).isFalse();

        TokenRecuperacionContrasena tNuevo = TokenRecuperacionContrasena.nuevo(5L, 30);
        assertThat(tNuevo.getIdUsuario()).isEqualTo(5L);
        assertThat(tNuevo.getToken()).isNotBlank();
    }

    @Test
    void testTokenVerificacionCorreo() {
        LocalDateTime now = LocalDateTime.now();
        TokenVerificacionCorreo tv = new TokenVerificacionCorreo(1L, 2L, "tok", now, now.plusHours(1), false);
        assertThat(tv.getIdToken()).isEqualTo(1L);
        assertThat(tv.getIdUsuario()).isEqualTo(2L);
        assertThat(tv.getToken()).isEqualTo("tok");
        assertThat(tv.getFechaExpiracion()).isEqualTo(now.plusHours(1));
        assertThat(tv.isUsado()).isFalse();
        assertThat(tv.getFechaCreacion()).isEqualTo(now);
        assertThat(tv.estaVigente(now)).isTrue();
        assertThat(tv.estaVigente(now.plusHours(2))).isFalse();

        tv.setUsado(true);
        assertThat(tv.estaVigente(now)).isFalse();

        TokenVerificacionCorreo tvNuevo = TokenVerificacionCorreo.nuevo(5L, 30);
        assertThat(tvNuevo.getIdUsuario()).isEqualTo(5L);
        assertThat(tvNuevo.getToken()).isNotBlank();
    }

    @Test
    void testRol() {
        Rol rol = new Rol();
        rol.setId((short) 1);
        rol.setNombre("ADMIN");
        rol.setDescripcion("Administrador");

        assertThat(rol.getId()).isEqualTo((short) 1);
        assertThat(rol.getNombre()).isEqualTo("ADMIN");
        assertThat(rol.getDescripcion()).isEqualTo("Administrador");

        Rol rol2 = new Rol((short) 2, "USER", "Usuario regular");
        assertThat(rol2.getId()).isEqualTo((short) 2);
    }
}
