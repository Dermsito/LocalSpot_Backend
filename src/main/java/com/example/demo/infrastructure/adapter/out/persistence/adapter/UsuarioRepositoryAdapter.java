package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.Usuario;
import com.example.demo.domain.port.UsuarioRepositoryPort;
import com.example.demo.infrastructure.adapter.out.persistence.entity.UsuarioJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.UsuarioJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UsuarioRepositoryAdapter implements UsuarioRepositoryPort {

    private final UsuarioJpaRepository usuarioJpaRepository;

    public UsuarioRepositoryAdapter(UsuarioJpaRepository usuarioJpaRepository) {
        this.usuarioJpaRepository = usuarioJpaRepository;
    }

    @Override
    public Optional<Usuario> findById(Long idUsuario) {
        return usuarioJpaRepository.findById(idUsuario)
                .map(this::toDomain);
    }

    @Override
    public Optional<Usuario> findByCorreo(String correo) {
        return usuarioJpaRepository.findByCorreo(correo)
                .map(this::toDomain);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioJpaEntity entity = toEntity(usuario);
        UsuarioJpaEntity saved = usuarioJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public boolean existsByCorreo(String correo) {
        return usuarioJpaRepository.existsByCorreo(correo);
    }

    private Usuario toDomain(UsuarioJpaEntity entity) {
        Usuario.TipoUsuario tipo = entity.getTipoUsuario() != null
                ? Usuario.TipoUsuario.valueOf(entity.getTipoUsuario().name())
                : Usuario.TipoUsuario.fromRolId(entity.getRolId());
        Usuario usuario = new Usuario(
                entity.getIdUsuario(),
                entity.getNombre(),
                entity.getCorreo(),
                entity.getContrasenaHash(),
                tipo,
                entity.getFechaRegistro()
        );
        if (entity.getRolId() != null) {
            usuario.setRolId(entity.getRolId());
        }
        return usuario;
    }

    private UsuarioJpaEntity toEntity(Usuario usuario) {
        UsuarioJpaEntity entity = new UsuarioJpaEntity();
        entity.setIdUsuario(usuario.getIdUsuario());
        entity.setNombre(usuario.getNombre());
        entity.setCorreo(usuario.getCorreo());
        entity.setContrasenaHash(usuario.getContrasenaHash());
        if (usuario.getTipoUsuario() != null) {
            entity.setTipoUsuario(UsuarioJpaEntity.TipoUsuarioJpa.valueOf(usuario.getTipoUsuario().name()));
        }
        entity.setRolId(usuario.getRolId());
        entity.setFechaRegistro(usuario.getFechaRegistro());
        return entity;
    }
}
