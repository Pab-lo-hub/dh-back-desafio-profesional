package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.Politica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PoliticaRepository extends JpaRepository<Politica, Long> {
    List<Politica> findByProductoId(Long productoId);
}
