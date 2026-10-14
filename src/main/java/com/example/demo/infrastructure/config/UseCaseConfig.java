package com.example.demo.infrastructure.config;

import com.example.demo.application.usecase.ActualizarPreferenciaUseCase;
import com.example.demo.application.usecase.AdministrarCategoriasUseCase;
import com.example.demo.application.usecase.AdministrarLugaresUseCase;
import com.example.demo.application.usecase.BuscarLugaresUseCase;
import com.example.demo.application.usecase.CalificarLugarUseCase;
import com.example.demo.application.usecase.ConsultarPreferenciasUsuarioUseCase;
import com.example.demo.application.usecase.CrearPreferenciaUseCase;
import com.example.demo.application.usecase.EliminarPreferenciaUseCase;
import com.example.demo.application.usecase.GenerarRecomendacionesUseCase;
import com.example.demo.application.usecase.GestionarEstablecimientosUseCase;
import com.example.demo.application.usecase.GestionarFavoritosUseCase;
import com.example.demo.application.usecase.GestionarReportesUseCase;
import com.example.demo.application.usecase.LoginUseCase;
import com.example.demo.application.usecase.ObtenerDetalleLugarUseCase;
import com.example.demo.application.usecase.RegistrarUsuarioUseCase;
import com.example.demo.domain.port.AiRecommendationPort;
import com.example.demo.domain.port.AuditoriaPort;
import com.example.demo.domain.port.CalificacionRepositoryPort;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.domain.port.FavoritoRepositoryPort;
import com.example.demo.domain.port.FotoLugarRepositoryPort;
import com.example.demo.domain.port.JwtServicePort;
import com.example.demo.domain.port.LugarRepositoryPort;
import com.example.demo.domain.port.PasswordEncoderPort;
import com.example.demo.domain.port.PreferenciaRepositoryPort;
import com.example.demo.domain.port.RecomendacionRepositoryPort;
import com.example.demo.domain.port.ReporteRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

    @Bean
    public RegistrarUsuarioUseCase registrarUsuarioUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
            PasswordEncoderPort passwordEncoderPort) {
        return new RegistrarUsuarioUseCase(usuarioRepositoryPort, passwordEncoderPort);
    }

    @Bean
    public LoginUseCase loginUseCase(UsuarioRepositoryPort usuarioRepositoryPort,
            PasswordEncoderPort passwordEncoderPort,
            JwtServicePort jwtServicePort) {
        return new LoginUseCase(usuarioRepositoryPort, passwordEncoderPort, jwtServicePort);
    }

    @Bean
    public CrearPreferenciaUseCase crearPreferenciaUseCase(PreferenciaRepositoryPort preferenciaRepositoryPort,
            UsuarioRepositoryPort usuarioRepositoryPort,
            CategoriaRepositoryPort categoriaRepositoryPort) {
        return new CrearPreferenciaUseCase(preferenciaRepositoryPort, usuarioRepositoryPort, categoriaRepositoryPort);
    }

    @Bean
    public ActualizarPreferenciaUseCase actualizarPreferenciaUseCase(
            PreferenciaRepositoryPort preferenciaRepositoryPort) {
        return new ActualizarPreferenciaUseCase(preferenciaRepositoryPort);
    }

    @Bean
    public ConsultarPreferenciasUsuarioUseCase consultarPreferenciasUsuarioUseCase(
            PreferenciaRepositoryPort preferenciaRepositoryPort,
            UsuarioRepositoryPort usuarioRepositoryPort) {
        return new ConsultarPreferenciasUsuarioUseCase(preferenciaRepositoryPort, usuarioRepositoryPort);
    }

    @Bean
    public EliminarPreferenciaUseCase eliminarPreferenciaUseCase(PreferenciaRepositoryPort preferenciaRepositoryPort) {
        return new EliminarPreferenciaUseCase(preferenciaRepositoryPort);
    }

    @Bean
    public BuscarLugaresUseCase buscarLugaresUseCase(LugarRepositoryPort lugarRepositoryPort) {
        return new BuscarLugaresUseCase(lugarRepositoryPort);
    }

    @Bean
    public ObtenerDetalleLugarUseCase obtenerDetalleLugarUseCase(
            LugarRepositoryPort lugarRepositoryPort,
            FotoLugarRepositoryPort fotoLugarRepositoryPort,
            CalificacionRepositoryPort calificacionRepositoryPort) {
        return new ObtenerDetalleLugarUseCase(lugarRepositoryPort, fotoLugarRepositoryPort, calificacionRepositoryPort);
    }

    @Bean
    public AdministrarLugaresUseCase administrarLugaresUseCase(
            LugarRepositoryPort lugarRepositoryPort,
            CategoriaRepositoryPort categoriaRepositoryPort,
            FotoLugarRepositoryPort fotoLugarRepositoryPort,
            AuditoriaPort auditoriaPort) {
        return new AdministrarLugaresUseCase(lugarRepositoryPort, categoriaRepositoryPort,
                fotoLugarRepositoryPort, auditoriaPort);
    }

    @Bean
    public AdministrarCategoriasUseCase administrarCategoriasUseCase(
            CategoriaRepositoryPort categoriaRepositoryPort,
            LugarRepositoryPort lugarRepositoryPort,
            AuditoriaPort auditoriaPort) {
        return new AdministrarCategoriasUseCase(categoriaRepositoryPort, lugarRepositoryPort, auditoriaPort);
    }

    @Bean
    public GestionarFavoritosUseCase gestionarFavoritosUseCase(
            FavoritoRepositoryPort favoritoRepositoryPort,
            LugarRepositoryPort lugarRepositoryPort) {
        return new GestionarFavoritosUseCase(favoritoRepositoryPort, lugarRepositoryPort);
    }

    @Bean
    public CalificarLugarUseCase calificarLugarUseCase(
            CalificacionRepositoryPort calificacionRepositoryPort,
            LugarRepositoryPort lugarRepositoryPort,
            AuditoriaPort auditoriaPort) {
        return new CalificarLugarUseCase(calificacionRepositoryPort, lugarRepositoryPort, auditoriaPort);
    }

    @Bean
    public GestionarReportesUseCase gestionarReportesUseCase(
            ReporteRepositoryPort reporteRepositoryPort,
            CalificacionRepositoryPort calificacionRepositoryPort,
            AuditoriaPort auditoriaPort) {
        return new GestionarReportesUseCase(reporteRepositoryPort, calificacionRepositoryPort, auditoriaPort);
    }

    @Bean
    public GestionarEstablecimientosUseCase gestionarEstablecimientosUseCase(
            LugarRepositoryPort lugarRepositoryPort,
            CategoriaRepositoryPort categoriaRepositoryPort,
            FotoLugarRepositoryPort fotoLugarRepositoryPort) {
        return new GestionarEstablecimientosUseCase(lugarRepositoryPort, categoriaRepositoryPort,
                fotoLugarRepositoryPort);
    }

    @Bean
    public GenerarRecomendacionesUseCase generarRecomendacionesUseCase(
            LugarRepositoryPort lugarRepositoryPort,
            PreferenciaRepositoryPort preferenciaRepositoryPort,
            RecomendacionRepositoryPort recomendacionRepositoryPort,
            CalificacionRepositoryPort calificacionRepositoryPort,
            AiRecommendationPort aiRecommendationPort) {
        return new GenerarRecomendacionesUseCase(lugarRepositoryPort, preferenciaRepositoryPort,
                recomendacionRepositoryPort, calificacionRepositoryPort, aiRecommendationPort);
    }
}
