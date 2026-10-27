package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CategoriaNoEncontradaException;
import com.example.demo.domain.exception.PreferenciaDuplicadaException;
import com.example.demo.domain.exception.UsuarioNoEncontradoException;
import com.example.demo.domain.model.Preferencia;
import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.domain.port.PreferenciaRepositoryPort;
import com.example.demo.domain.port.UsuarioRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrearPreferenciaUseCaseTest {

    @Mock
    private PreferenciaRepositoryPort preferenciaRepositoryPort;

    @Mock
    private UsuarioRepositoryPort usuarioRepositoryPort;

    @Mock
    private CategoriaRepositoryPort categoriaRepositoryPort;

    @InjectMocks
    private CrearPreferenciaUseCase crearPreferenciaUseCase;

    @Test
    void ejecutar_cuandoTodoValido_debeCrear() {
        when(usuarioRepositoryPort.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(categoriaRepositoryPort.existsById(1L)).thenReturn(true);
        when(preferenciaRepositoryPort.findByIdUsuarioAndIdCategoria(1L, 1L))
                .thenReturn(Optional.empty());
        when(preferenciaRepositoryPort.save(any(Preferencia.class))).thenAnswer(inv -> inv.getArgument(0));

        Preferencia p = crearPreferenciaUseCase.ejecutar(1L, 1L, new BigDecimal("50000"));

        assertThat(p.getIdUsuario()).isEqualTo(1L);
        assertThat(p.getIdCategoria()).isEqualTo(1L);
        assertThat(p.getPresupuestoMax()).isEqualByComparingTo("50000");
    }

    @Test
    void ejecutar_cuandoUsuarioNoExiste_debeLanzarExcepcion() {
        when(usuarioRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> crearPreferenciaUseCase.ejecutar(99L, 1L, BigDecimal.TEN))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void ejecutar_cuandoCategoriaNoExiste_debeLanzarExcepcion() {
        when(usuarioRepositoryPort.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(categoriaRepositoryPort.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> crearPreferenciaUseCase.ejecutar(1L, 99L, BigDecimal.TEN))
                .isInstanceOf(CategoriaNoEncontradaException.class);
    }

    @Test
    void ejecutar_cuandoPreferenciaExiste_debeLanzarExcepcionDuplicada() {
        when(usuarioRepositoryPort.findById(1L)).thenReturn(Optional.of(new Usuario()));
        when(categoriaRepositoryPort.existsById(1L)).thenReturn(true);
        Preferencia existente = new Preferencia();
        existente.setIdUsuario(1L);
        existente.setIdCategoria(1L);
        existente.setPresupuestoMax(new BigDecimal("10000"));
        when(preferenciaRepositoryPort.findByIdUsuarioAndIdCategoria(1L, 1L))
                .thenReturn(Optional.of(existente));

        assertThatThrownBy(() -> crearPreferenciaUseCase.ejecutar(1L, 1L, new BigDecimal("80000")))
                .isInstanceOf(PreferenciaDuplicadaException.class);
        verify(preferenciaRepositoryPort, never()).save(any());
    }
}
