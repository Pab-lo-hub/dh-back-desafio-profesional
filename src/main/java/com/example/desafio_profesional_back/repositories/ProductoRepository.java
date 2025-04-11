package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository is an interface that provides access to data in a database
 */
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    boolean existsByNombre(String nombre);
}
