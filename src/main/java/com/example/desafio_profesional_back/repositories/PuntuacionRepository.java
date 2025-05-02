package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.Puntuacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuntuacionRepository extends JpaRepository<Puntuacion, Long> {
    List<Puntuacion> findByProductoId(Long productoId);
    boolean existsByProducto_IdAndUser_Id(Long productoId, Long userId);
}