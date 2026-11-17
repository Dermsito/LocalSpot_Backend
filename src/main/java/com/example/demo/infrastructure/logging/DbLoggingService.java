package com.example.demo.infrastructure.logging;

import com.example.demo.infrastructure.adapter.out.persistence.entity.LogEventoJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.entity.LogHttpJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.LogEventoJpaRepository;
import com.example.demo.infrastructure.adapter.out.persistence.repository.LogHttpJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

/**
 * Servicio asíncrono para almacenar logs HTTP y de eventos en base de datos.
 * La ejecución asíncrona asegura cero impacto en la latencia de las peticiones.
 */
@Service
public class DbLoggingService {

    private static final Logger log = LoggerFactory.getLogger(DbLoggingService.class);

    private final LogHttpJpaRepository logHttpRepository;
    private final LogEventoJpaRepository logEventoRepository;

    public DbLoggingService(LogHttpJpaRepository logHttpRepository,
                            LogEventoJpaRepository logEventoRepository) {
        this.logHttpRepository = logHttpRepository;
        this.logEventoRepository = logEventoRepository;
    }

    @Async
    public void registrarLogHttp(String correlationId, String metodo, String uri,
                                 String queryParams, int statusCode, long tiempoMs,
                                 String ipOrigen, String usuarioCorreo) {
        try {
            LogHttpJpaEntity entity = new LogHttpJpaEntity(
                    correlationId,
                    metodo,
                    uri,
                    queryParams,
                    statusCode,
                    tiempoMs,
                    ipOrigen,
                    usuarioCorreo,
                    LocalDateTime.now()
            );
            logHttpRepository.save(entity);
        } catch (Exception e) {
            log.error("Fallo al persistir log_http en BD: {}", e.getMessage());
        }
    }

    @Async
    public void registrarLogEvento(String nivel, String origen, String mensaje,
                                   Throwable throwable, String correlationId, String usuarioCorreo) {
        try {
            String detalles = null;
            if (throwable != null) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                throwable.printStackTrace(pw);
                detalles = sw.toString();
            }

            LogEventoJpaEntity entity = new LogEventoJpaEntity(
                    correlationId,
                    nivel,
                    origen,
                    mensaje,
                    detalles,
                    usuarioCorreo,
                    LocalDateTime.now()
            );
            logEventoRepository.save(entity);
        } catch (Exception e) {
            log.error("Fallo al persistir log_evento en BD: {}", e.getMessage());
        }
    }

    public void registrarError(String origen, String mensaje, Throwable throwable) {
        String correlationId = MDC.get("correlationId");
        String usuario = obtenerUsuarioActual();
        registrarLogEvento("ERROR", origen, mensaje, throwable, correlationId, usuario);
    }

    public void registrarWarn(String origen, String mensaje) {
        String correlationId = MDC.get("correlationId");
        String usuario = obtenerUsuarioActual();
        registrarLogEvento("WARN", origen, mensaje, null, correlationId, usuario);
    }

    public void registrarInfo(String origen, String mensaje) {
        String correlationId = MDC.get("correlationId");
        String usuario = obtenerUsuarioActual();
        registrarLogEvento("INFO", origen, mensaje, null, correlationId, usuario);
    }

    private String obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return null;
    }
}
