package com.example.demo.infrastructure.adapter.out.persistence.adapter;

import com.example.demo.domain.model.FotoLugar;
import com.example.demo.domain.port.FotoLugarRepositoryPort;
import com.example.demo.infrastructure.adapter.out.persistence.entity.FotoLugarJpaEntity;
import com.example.demo.infrastructure.adapter.out.persistence.repository.FotoLugarJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FotoLugarRepositoryAdapter implements FotoLugarRepositoryPort {

    private final FotoLugarJpaRepository fotoLugarJpaRepository;

    public FotoLugarRepositoryAdapter(FotoLugarJpaRepository fotoLugarJpaRepository) {
        this.fotoLugarJpaRepository = fotoLugarJpaRepository;
    }

    @Override
    public List<FotoLugar> saveAll(List<FotoLugar> fotos) {
        List<FotoLugarJpaEntity> entities = fotos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
        return fotoLugarJpaRepository.saveAll(entities).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<FotoLugar> findByLugarId(Long idLugar) {
        return fotoLugarJpaRepository.findByIdLugar(idLugar).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByLugarId(Long idLugar) {
        fotoLugarJpaRepository.deleteByIdLugar(idLugar);
    }

    private FotoLugar toDomain(FotoLugarJpaEntity entity) {
        FotoLugar foto = new FotoLugar();
        foto.setIdFotoLugar(entity.getIdFotoLugar());
        foto.setIdLugar(entity.getIdLugar());
        foto.setUrlFoto(entity.getUrlFoto());
        foto.setFechaSubida(entity.getFechaSubida());
        return foto;
    }

    private FotoLugarJpaEntity toEntity(FotoLugar foto) {
        FotoLugarJpaEntity entity = new FotoLugarJpaEntity();
        entity.setIdFotoLugar(foto.getIdFotoLugar());
        entity.setIdLugar(foto.getIdLugar());
        entity.setUrlFoto(foto.getUrlFoto());
        entity.setFechaSubida(foto.getFechaSubida());
        return entity;
    }
}
