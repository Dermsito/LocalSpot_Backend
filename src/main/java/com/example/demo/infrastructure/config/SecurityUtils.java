package com.example.demo.infrastructure.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Utilidad para obtener informacion del usuario autenticado desde el SecurityContext.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Obtiene el ID del usuario autenticado.
     *
     * @return ID del usuario o null si no hay autenticacion
     */
    public static Long getCurrentUsuarioId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) {
            return (Long) principal;
        }
        if (principal instanceof String) {
            try {
                return Long.valueOf((String) principal);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Obtiene el rol del usuario autenticado (sin el prefijo ROLE_).
     *
     * @return nombre del rol o null si no hay autenticacion
     */
    public static String getCurrentRol() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        return authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .filter(a -> a.startsWith("ROLE_"))
                .map(a -> a.substring("ROLE_".length()))
                .findFirst()
                .orElse(null);
    }
}
