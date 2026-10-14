package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.LoginUseCase;
import com.example.demo.application.usecase.RegistrarUsuarioUseCase;
import com.example.demo.domain.model.Usuario;
import com.example.demo.infrastructure.adapter.in.web.dto.ErrorResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.LoginRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.LoginResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.RegistroUsuarioRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.UsuarioResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@Tag(name = "Usuarios", description = "Endpoints publicos para gestion de usuarios (registro, login, logout)")
public class UsuarioController {

    private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
    private final LoginUseCase loginUseCase;

    public UsuarioController(RegistrarUsuarioUseCase registrarUsuarioUseCase,
                             LoginUseCase loginUseCase) {
        this.registrarUsuarioUseCase = registrarUsuarioUseCase;
        this.loginUseCase = loginUseCase;
    }

    @PostMapping("/registro")
    @Operation(summary = "Crear un nuevo usuario", description = "Registra una cuenta de usuario en el sistema. No requiere autenticacion. El tipo de usuario por defecto es 'final'.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuario creado correctamente",
                    content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos de entrada invalidos (nombre/correo/contrasena faltantes o formato incorrecto)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "El correo ya se encuentra registrado en el sistema",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<UsuarioResponse> registrar(
            @Valid @RequestBody
            @Parameter(description = "Datos del nuevo usuario", required = true,
                    schema = @Schema(implementation = RegistroUsuarioRequest.class))
            RegistroUsuarioRequest request) {
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
    @Operation(summary = "Iniciar sesion", description = "Autentica un usuario con correo y contrasena y devuelve un token JWT para acceder a endpoints protegidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticacion exitosa",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "Cuerpo de solicitud invalido",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "401", description = "Credenciales invalidas (correo inexistente o contrasena incorrecta)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody
            @Parameter(description = "Credenciales de acceso", required = true,
                    schema = @Schema(implementation = LoginRequest.class))
            LoginRequest request) {
        LoginUseCase.ResultadoLogin resultado = loginUseCase.ejecutar(request.getCorreo(), request.getContrasena());
        LoginResponse response = new LoginResponse(
                resultado.token(),
                resultado.expiracionMs(),
                resultado.id(),
                resultado.nombre(),
                resultado.rol()
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesion", description = "Endopint de logout (JWT es stateless asi que la sesion se cierra del lado del cliente eliminando el token).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout exitoso")
    })
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

