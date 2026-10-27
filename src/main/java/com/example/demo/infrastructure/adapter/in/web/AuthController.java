package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.application.usecase.LoginUseCase;
import com.example.demo.application.usecase.ReenviarVerificacionUseCase;
import com.example.demo.application.usecase.RegistrarUsuarioUseCase;
import com.example.demo.application.usecase.VerificarCorreoUseCase;
import com.example.demo.domain.model.Usuario;
import com.example.demo.infrastructure.adapter.in.web.dto.ErrorResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.LoginRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.LoginResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.MensajeResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.ReenviarVerificacionRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.RegistroUsuarioRequest;
import com.example.demo.infrastructure.adapter.in.web.dto.UsuarioResponse;
import com.example.demo.infrastructure.adapter.in.web.dto.VerificarCorreoRequest;
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
@RequestMapping("/api/auth")
@Tag(name = "Auth", description = "Endpoints de autenticacion (registro, login, logout)")
public class AuthController {

        private final RegistrarUsuarioUseCase registrarUsuarioUseCase;
        private final LoginUseCase loginUseCase;
        private final VerificarCorreoUseCase verificarCorreoUseCase;
        private final ReenviarVerificacionUseCase reenviarVerificacionUseCase;

        public AuthController(RegistrarUsuarioUseCase registrarUsuarioUseCase,
                        LoginUseCase loginUseCase,
                        VerificarCorreoUseCase verificarCorreoUseCase,
                        ReenviarVerificacionUseCase reenviarVerificacionUseCase) {
                this.registrarUsuarioUseCase = registrarUsuarioUseCase;
                this.loginUseCase = loginUseCase;
                this.verificarCorreoUseCase = verificarCorreoUseCase;
                this.reenviarVerificacionUseCase = reenviarVerificacionUseCase;
        }

        @PostMapping("/register")
        @Operation(summary = "Registrar un nuevo usuario", description = "Crea una cuenta de usuario con rol FINAL por defecto y envia un correo "
                        + "de verificacion. El usuario no podra iniciar sesion hasta verificar su correo.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "201", description = "Usuario creado", content = @Content(schema = @Schema(implementation = UsuarioResponse.class))),
                        @ApiResponse(responseCode = "400", description = "Datos invalidos", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "409", description = "Correo duplicado", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<UsuarioResponse> register(
                        @Valid @RequestBody @Parameter(description = "Datos del nuevo usuario", required = true) RegistroUsuarioRequest request) {
                Usuario usuario = registrarUsuarioUseCase.ejecutar(
                                request.getNombre(),
                                request.getCorreo(),
                                request.getContrasena(),
                                Usuario.TipoUsuario.FINAL);
                return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(usuario));
        }

        @PostMapping("/login")
        @Operation(summary = "Iniciar sesion", description = "Autentica con correo y contrasena y devuelve un JWT.")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = "200", description = "Login exitoso", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
                        @ApiResponse(responseCode = "401", description = "Credenciales invalidas", content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
                        @ApiResponse(responseCode = "403", description = "Correo sin verificar", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
        })
        public ResponseEntity<LoginResponse> login(
                        @Valid @RequestBody @Parameter(description = "Credenciales de acceso", required = true) LoginRequest request) {
                LoginUseCase.ResultadoLogin resultado = loginUseCase.ejecutar(request.getCorreo(),
                                request.getContrasena());
                LoginResponse response = new LoginResponse(
                                resultado.token(),
                                resultado.expiracionMs(),
                                resultado.id(),
                                resultado.nombre(),
                                resultado.rol());
                return ResponseEntity.ok(response);
        }

        @PostMapping("/logout")
    @Operation(summary = "Cerrar sesion", description = "Logout stateless (el cliente elimina el token).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Logout exitoso")
    })
    public ResponseEntity<Void> logout() {
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/verificar-correo")
    @Operation(summary = "Verificar correo",
            description = "Confirma la cuenta con el token recibido por correo y habilita el login.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Correo verificado",
                    content = @Content(schema = @Schema(implementation = MensajeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Token invalido, usado o expirado",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MensajeResponse> verificarCorreo(
            @Valid @RequestBody
            @Parameter(description = "Token de verificacion recibido por correo", required = true)
            VerificarCorreoRequest request) {
        verificarCorreoUseCase.ejecutar(request.getToken());
        return ResponseEntity.ok(new MensajeResponse("Correo verificado correctamente"));
    }

    @PostMapping("/reenviar-verificacion")
    @Operation(summary = "Reenviar verificacion",
            description = "Genera un nuevo token de verificacion y lo envia por correo. "
                    + "La respuesta es la misma exista o no la cuenta, para no revelar su existencia.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Solicitud procesada",
                    content = @Content(schema = @Schema(implementation = MensajeResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos invalidos",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<MensajeResponse> reenviarVerificacion(
            @Valid @RequestBody
            @Parameter(description = "Correo registrado", required = true)
            ReenviarVerificacionRequest request) {
        reenviarVerificacionUseCase.ejecutar(request.getCorreo());
        return ResponseEntity.ok(new MensajeResponse(
                "Si el correo esta registrado y sin verificar, se envio un nuevo enlace de verificacion"));
    }

        private UsuarioResponse toResponse(Usuario usuario) {
                return new UsuarioResponse(
                                usuario.getIdUsuario(),
                                usuario.getNombre(),
                                usuario.getCorreo(),
                                usuario.getTipoUsuario() != null ? usuario.getTipoUsuario().name().toLowerCase() : null,
                                usuario.getFechaRegistro());
        }
}
