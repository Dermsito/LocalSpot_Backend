package com.example.demo.application.usecase;

import com.example.demo.domain.exception.CategoriaEnUsoException;
import com.example.demo.domain.exception.CategoriaNoEncontradaException;
import com.example.demo.domain.model.Categoria;
import com.example.demo.domain.port.AuditoriaPort;
import com.example.demo.domain.port.CategoriaRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdministrarCategoriasUseCaseTest {

    @Mock
    private CategoriaRepositoryPort categoriaRepositoryPort;

    @Mock
    private LugarRepositoryPort lugarRepositoryPort;

    @Mock
    private AuditoriaPort auditoriaPort;

    @InjectMocks
    private AdministrarCategoriasUseCase administrarCategoriasUseCase;

    @Test
    void crear_conNombreUnico_debeGuardar() {
        when(categoriaRepositoryPort.existsByNombreCategoria("Restaurantes")).thenReturn(false);
        when(categoriaRepositoryPort.save(any(Categoria.class))).thenAnswer(inv -> {
            Categoria c = inv.getArgument(0);
            c.setIdCategoria(1L);
            return c;
        });

        Categoria categoria = administrarCategoriasUseCase.crear(1L, "Restaurantes");

        assertThat(categoria.getNombreCategoria()).isEqualTo("Restaurantes");
        assertThat(categoria.getIdCategoria()).isEqualTo(1L);
    }

    @Test
    void crear_conNombreDuplicado_debeLanzarExcepcion() {
        when(categoriaRepositoryPort.existsByNombreCategoria("Restaurantes")).thenReturn(true);

        assertThatThrownBy(() -> administrarCategoriasUseCase.crear(1L, "Restaurantes"))
                .isInstanceOf(CategoriaEnUsoException.class);
    }

    @Test
    void actualizar_cuandoExiste_debeActualizar() {
        Categoria existente = new Categoria();
        existente.setIdCategoria(1L);
        existente.setNombreCategoria("Viejo");
        when(categoriaRepositoryPort.findById(1L)).thenReturn(Optional.of(existente));
        when(categoriaRepositoryPort.existsByNombreCategoriaAndIdNot("Nuevo", 1L)).thenReturn(false);
        when(categoriaRepositoryPort.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        Categoria actualizada = administrarCategoriasUseCase.actualizar(1L, 1L, "Nuevo");

        assertThat(actualizada.getNombreCategoria()).isEqualTo("Nuevo");
    }

    @Test
    void actualizar_cuandoNoExiste_debeLanzarExcepcion() {
        when(categoriaRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> administrarCategoriasUseCase.actualizar(1L, 99L, "Nuevo"))
                .isInstanceOf(CategoriaNoEncontradaException.class);
    }

    @Test
    void eliminar_cuandoNoEstaEnUso_debeEliminar() {
        when(categoriaRepositoryPort.existsById(1L)).thenReturn(true);
        when(lugarRepositoryPort.existsByCategoriaId(1L)).thenReturn(false);

        administrarCategoriasUseCase.eliminar(1L, 1L);

        verify(categoriaRepositoryPort, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoEstaEnUso_debeLanzarExcepcion() {
        when(categoriaRepositoryPort.existsById(1L)).thenReturn(true);
        when(lugarRepositoryPort.existsByCategoriaId(1L)).thenReturn(true);

        assertThatThrownBy(() -> administrarCategoriasUseCase.eliminar(1L, 1L))
                .isInstanceOf(CategoriaEnUsoException.class);
    }
}
