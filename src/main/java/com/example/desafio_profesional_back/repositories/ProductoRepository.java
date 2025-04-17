package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository is an interface that provides access to data in a database
 * Repositorio para operaciones CRUD con la entidad Producto.
 */
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    boolean existsByNombre(String nombre);
}
