package com.example.demo.infrastructure.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * Configuración centralizada para todos los mappers MapStruct del proyecto.
 * - componentModel = "spring": los mappers se gestionan como beans de Spring.
 * - unmappedTargetPolicy = IGNORE: no falla si el target tiene campos sin mapear.
 */
@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface MapStructConfig {
}
