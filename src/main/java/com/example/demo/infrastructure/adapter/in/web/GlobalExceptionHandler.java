package com.example.demo.infrastructure.adapter.in.web;

import com.example.demo.domain.exception.CalificacionDuplicadaException;
import com.example.demo.domain.exception.CalificacionNoEncontradaException;
import com.example.demo.domain.exception.CategoriaEnUsoException;
import com.example.demo.domain.exception.CategoriaNoEncontradaException;
import com.example.demo.domain.exception.CorreoDuplicadoException;
import com.example.demo.domain.exception.CorreoNoVerificadoException;
import com.example.demo.domain.exception.CredencialesInvalidasException;
import com.example.demo.domain.exception.EstadoReporteInvalidoException;
import com.example.demo.domain.exception.FavoritoDuplicadoException;
import com.example.demo.domain.exception.LugarNoEncontradoException;
import com.example.demo.domain.exception.OperacionNoAutorizadaException;
import com.example.demo.domain.exception.PreferenciaDuplicadaException;
import com.example.demo.domain.exception.PreferenciaNoEncontradaException;
import com.example.demo.domain.exception.RecomendacionSinPreferenciasException;
import com.example.demo.domain.exception.ReporteDuplicadoException;
import com.example.demo.domain.exception.ReporteNoEncontradoException;
import com.example.demo.domain.exception.TokenVerificacionInvalidoException;
import com.example.demo.domain.exception.UsuarioNoEncontradoException;
import com.example.demo.domain.exception.VerificacionInvalidaException;
import com.example.demo.infrastructure.adapter.in.web.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // 409 Conflict
    @ExceptionHandler({ CorreoDuplicadoException.class, FavoritoDuplicadoException.class,
            CalificacionDuplicadaException.class, ReporteDuplicadoException.class,
            CategoriaEnUsoException.class, PreferenciaDuplicadaException.class })
    public ResponseEntity<ErrorResponse> handleConflicto(RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // 401 Unauthorized
    @ExceptionHandler({ CredencialesInvalidasException.class, AuthenticationException.class })
    public ResponseEntity<ErrorResponse> handleNoAutenticado(Exception ex, HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "Autenticacion requerida o credenciales invalidas", request);
    }

    // 403 Forbidden
    @ExceptionHandler({ OperacionNoAutorizadaException.class, CorreoNoVerificadoException.class })
    public ResponseEntity<ErrorResponse> handleNoAutorizado(RuntimeException ex,
            HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    // 404 Not Found
    @ExceptionHandler({ UsuarioNoEncontradoException.class, CategoriaNoEncontradaException.class,
            PreferenciaNoEncontradaException.class, LugarNoEncontradoException.class,
            CalificacionNoEncontradaException.class, ReporteNoEncontradoException.class })
    public ResponseEntity<ErrorResponse> handleNoEncontrado(RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // 400 Bad Request - validaciones de negocio
    @ExceptionHandler({ EstadoReporteInvalidoException.class, VerificacionInvalidaException.class,
            RecomendacionSinPreferenciasException.class, TokenVerificacionInvalidoException.class,
            IllegalArgumentException.class })
    public ResponseEntity<ErrorResponse> handleSolicitudInvalida(RuntimeException ex, HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // 400 Bad Request - validaciones de Bean Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidacion(MethodArgumentNotValidException ex,
            HttpServletRequest request) {
        List<ErrorResponse.FieldError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ErrorResponse.FieldError(fe.getField(), fe.getDefaultMessage()))
                .collect(Collectors.toList());
        ErrorResponse response = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Error de validacion",
                request.getRequestURI(),
                errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 400 Bad Request - cuerpo invalido
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleCuerpoInvalido(HttpMessageNotReadableException ex,
            HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "Cuerpo de solicitud invalido o ausente", request);
    }

    // 500 Internal Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenerico(Exception ex, HttpServletRequest request) {
        log.error("Error inesperado en {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error inesperado", request);
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                status.value(),
                message,
                request.getRequestURI());
        return ResponseEntity.status(status).body(response);
    }
}
