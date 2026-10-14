package com.example.demo.application.usecase;

import com.example.demo.domain.exception.FavoritoDuplicadoException;
import com.example.demo.domain.exception.LugarNoEncontradoException;
import com.example.demo.domain.model.Favorito;
import com.example.demo.domain.port.FavoritoRepositoryPort;
import com.example.demo.domain.port.LugarRepositoryPort;

import java.time.LocalDateTime;
import java.util.List;

/**
 * RF-07: Gestion de favoritos (agregar, quitar, listar).
 * Sin duplicados.
 */
public class GestionarFavoritosUseCase {

    private final FavoritoRepositoryPort favoritoRepositoryPort;
    private final LugarRepositoryPort lugarRepositoryPort;

    public GestionarFavoritosUseCase(FavoritoRepositoryPort favoritoRepositoryPort,
                                      LugarRepositoryPort lugarRepositoryPort) {
        this.favoritoRepositoryPort = favoritoRepositoryPort;
        this.lugarRepositoryPort = lugarRepositoryPort;
    }

    public Favorito agregar(Long idUsuario, Long idLugar) {
        if (!lugarRepositoryPort.findById(idLugar).isPresent()) {
            throw new LugarNoEncontradoException("Lugar no encontrado con id: " + idLugar);
        }
        if (favoritoRepositoryPort.existsByUsuarioIdAndLugarId(idUsuario, idLugar)) {
            throw new FavoritoDuplicadoException("El lugar ya esta en favoritos");
        }
        Favorito favorito = new Favorito(idUsuario, idLugar, LocalDateTime.now());
        return favoritoRepositoryPort.save(favorito);
    }

    public void quitar(Long idUsuario, Long idLugar) {
        if (!favoritoRepositoryPort.existsByUsuarioIdAndLugarId(idUsuario, idLugar)) {
            throw new LugarNoEncontradoException("El lugar no esta en favoritos");
        }
        favoritoRepositoryPort.deleteByUsuarioIdAndLugarId(idUsuario, idLugar);
    }

    public List<Favorito> listar(Long idUsuario) {
        return favoritoRepositoryPort.findByUsuarioId(idUsuario);
    }
}
