package com.example.demo.infrastructure.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro que registra métricas y detalles de cada solicitud HTTP entrante en la BD.
 * Se ejecuta justo después de MdcFilter para contar con el correlationId.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
public class HttpLoggingFilter extends OncePerRequestFilter {

    private final DbLoggingService dbLoggingService;

    public HttpLoggingFilter(DbLoggingService dbLoggingService) {
        this.dbLoggingService = dbLoggingService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        long inicio = System.currentTimeMillis();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long duracion = System.currentTimeMillis() - inicio;
            String uri = request.getRequestURI();

            // Omitir endpoints ruidosos como health checks de Actuator si se desea, o registrar todo
            if (!uri.startsWith("/actuator/health")) {
                String correlationId = MDC.get("correlationId");
                String metodo = request.getMethod();
                String query = request.getQueryString();
                int status = response.getStatus();
                String ip = obtenerIpCliente(request);
                String usuario = obtenerUsuarioActual();

                dbLoggingService.registrarLogHttp(
                        correlationId,
                        metodo,
                        uri,
                        query,
                        status,
                        duracion,
                        ip,
                        usuario
                );
            }
        }
    }

    private String obtenerIpCliente(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private String obtenerUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            return auth.getName();
        }
        return null;
    }
}
