package com.example.demo.domain.port;

import com.example.demo.domain.model.Favorito;

import java.util.List;
import java.util.Optional;

public interface FavoritoRepositoryPort {
    Favorito save(Favorito favorito);
    void deleteByUsuarioIdAndLugarId(Long idUsuario, Long idLugar);
    List<Favorito> findByUsuarioId(Long idUsuario);
    boolean existsByUsuarioIdAndLugarId(Long idUsuario, Long idLugar);
    Optional<Favorito> findByUsuarioIdAndLugarId(Long idUsuario, Long idLugar);
}
