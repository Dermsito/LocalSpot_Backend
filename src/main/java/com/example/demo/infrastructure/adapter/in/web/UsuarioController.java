package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.LoginUseCase;
import com.example.demo.application.usecase.RegistrarUsuarioUseCase;
import com.example.demo.domain.model.Usuario;
import com.example.demo.infrastructure.adapter.in.web.dto.LoginRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.LoginResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.RegistroUsuarioRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.UsuarioResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final LoginUseCase loginUseCase;

    public UsuarioController(RegistrarUsuarioUseCase registrarUsuarioUseCase,
                             LoginUseCase loginUseCase) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/registro")
    public ResponseEntity<UsuarioResponse> registrar(@Valid @RequestBody RegistroUsuarioRequest request) {
        Usuario.TipoUsuario tipo = parseTipoUsuario(request.getTipoUsuario());
        Usuario usuario = registrarUsuarioUseCase.ejecutar(
                request.getNombre(),
                request.getCorreo(),
                request.getContrasena(),
                tipo
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = loginUseCase.ejecutar(request.getCorreo(), request.getContrasena());
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    private Usuario.TipoUsuario parseTipoUsuario(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return Usuario.TipoUsuario.FINAL;
        }
        try {
            return Usuario.TipoUsuario.valueOf(tipo.toUpperCase());
        } catch (IllegalArgumentException e) {
            return Usuario.TipoUsuario.FINAL;
        }
    }

    private UsuarioResponse toResponse(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getIdUsuario(),
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getTipoUsuario() != null ? usuario.getTipoUsuario().name().toLowerCase() : null,
                usuario.getFechaRegistro()
        );
    }
}
