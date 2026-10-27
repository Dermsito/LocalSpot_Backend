package com.example.demo.infrastructure.bootstrap;

import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.UsuarioRepositoryPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Crea el usuario administrador inicial si no existe.
 * Credenciales: ADMIN_EMAIL / ADMIN_PASSWORD (env vars).
 * Si las variables no estan definidas, no crea admin y registra advertencia.
 */
@Component
public class AdminDataInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminDataInitializer.class);

    private final UsuarioRepositoryPort usuarioRepositoryPort;
    private final PasswordEncoder passwordEncoder;

    @Value("${localspot.admin.email:}")
    private String adminEmail;

    @Value("${localspot.admin.password:}")
    private String adminPassword;

    public AdminDataInitializer(UsuarioRepositoryPort usuarioRepositoryPort,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepositoryPort = usuarioRepositoryPort;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (adminEmail == null || adminEmail.isBlank()
                || adminPassword == null || adminPassword.isBlank()) {
            log.warn("ADMIN_EMAIL / ADMIN_PASSWORD no configurados. No se crea usuario administrador por defecto.");
            return;
        }

        if (usuarioRepositoryPort.existsByCorreo(adminEmail)) {
            log.info("Usuario administrador ya existe: {}", adminEmail);
            return;
        }

        Usuario admin = new Usuario();
        admin.setNombre("Administrador LocalSpot");
        admin.setCorreo(adminEmail);
        admin.setContrasenaHash(passwordEncoder.encode(adminPassword));
        admin.setTipoUsuario(Usuario.TipoUsuario.ADMINISTRADOR);
        admin.setFechaRegistro(LocalDateTime.now());
        admin.setVerificado(true);
        admin.setFechaVerificacion(LocalDateTime.now());

        usuarioRepositoryPort.save(admin);
        log.info("Usuario administrador creado exitosamente: {}", adminEmail);
    }
}
