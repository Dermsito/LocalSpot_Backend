package com.example.demo.domain.port;

import com.example.demo.domain.model.FotoLugar;

import java.util.List;

public interface FotoLugarRepositoryPort {
    List<FotoLugar> saveAll(List<FotoLugar> fotos);
    List<FotoLugar> findByLugarId(Long idLugar);
    void deleteByLugarId(Long idLugar);
}
