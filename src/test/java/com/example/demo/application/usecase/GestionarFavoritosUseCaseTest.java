package com.example.demo.application.usecase;

import com.example.demo.domain.exception.FavoritoDuplicadoException;
import com.example.demo.domain.exception.LugarNoEncontradoException;
import com.example.demo.domain.model.Favorito;
import com.example.demo.domain.model.Lugar;
import com.example.demo.domain.port.FavoritoRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GestionarFavoritosUseCaseTest {

    @Mock
    private FavoritoRepositoryPort favoritoRepositoryPort;

    @Mock
    private LugarRepositoryPort lugarRepositoryPort;

    @InjectMocks
    private GestionarFavoritosUseCase gestionarFavoritosUseCase;

    @Test
    void agregar_cuandoLugarExisteYNoEsFavorito_debeGuardar() {
        when(lugarRepositoryPort.findById(1L)).thenReturn(Optional.of(new Lugar()));
        when(favoritoRepositoryPort.existsByUsuarioIdAndLugarId(1L, 1L)).thenReturn(false);
        when(favoritoRepositoryPort.save(any(Favorito.class))).thenAnswer(inv -> inv.getArgument(0));

        Favorito favorito = gestionarFavoritosUseCase.agregar(1L, 1L);

        assertThat(favorito.getIdUsuario()).isEqualTo(1L);
        assertThat(favorito.getIdLugar()).isEqualTo(1L);
    }

    @Test
    void agregar_cuandoLugarNoExiste_debeLanzarExcepcion() {
        when(lugarRepositoryPort.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> gestionarFavoritosUseCase.agregar(1L, 99L))
                .isInstanceOf(LugarNoEncontradoException.class);
    }

    @Test
    void agregar_cuandoYaEsFavorito_debeLanzarExcepcion() {
        when(lugarRepositoryPort.findById(1L)).thenReturn(Optional.of(new Lugar()));
        when(favoritoRepositoryPort.existsByUsuarioIdAndLugarId(1L, 1L)).thenReturn(true);

        assertThatThrownBy(() -> gestionarFavoritosUseCase.agregar(1L, 1L))
                .isInstanceOf(FavoritoDuplicadoException.class);
    }

    @Test
    void listar_debeRetornarFavoritosDelUsuario() {
        Favorito f = new Favorito(1L, 1L, null);
        when(favoritoRepositoryPort.findByUsuarioId(1L)).thenReturn(List.of(f));

        List<Favorito> favoritos = gestionarFavoritosUseCase.listar(1L);

        assertThat(favoritos).hasSize(1);
    }

    @Test
    void quitar_debeEliminarFavorito() {
        when(favoritoRepositoryPort.existsByUsuarioIdAndLugarId(1L, 1L)).thenReturn(true);
        gestionarFavoritosUseCase.quitar(1L, 1L);
        verify(favoritoRepositoryPort, times(1)).deleteByUsuarioIdAndLugarId(1L, 1L);
    }
}
