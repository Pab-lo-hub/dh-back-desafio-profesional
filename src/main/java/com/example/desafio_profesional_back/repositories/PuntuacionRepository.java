package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.Puntuacion;
import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PuntuacionRepository extends JpaRepository<Puntuacion, Long> {
    boolean existsByUsuarioAndProducto(User usuario, Producto producto);
}
