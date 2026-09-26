package com.example.demo.domain.port;

import com.example.demo.domain.model.Usuario;

import java.util.Optional;

public interface UsuarioRepositoryPort {
    Optional<Usuario> findById(Long idUsuario);
    Optional<Usuario> findByCorreo(String correo);
    Usuario save(Usuario usuario);
    boolean existsByCorreo(String correo);
}
