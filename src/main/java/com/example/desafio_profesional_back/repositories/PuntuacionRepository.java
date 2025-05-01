package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.Puntuacion;
import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PuntuacionRepository extends JpaRepository<Puntuacion, Long> {
    List<Puntuacion> findByProductoId(Long productoId);
    boolean existsByProductoIdAndUsuarioId(Long productoId, Long usuarioId);
}
