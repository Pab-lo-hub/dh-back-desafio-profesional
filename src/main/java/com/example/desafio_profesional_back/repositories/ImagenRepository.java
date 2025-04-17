package com.example.desafio_profesional_back.repositories;
import com.example.desafio_profesional_back.models.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para operaciones CRUD con la entidad Imagen.
 */
@Repository
public interface ImagenRepository extends JpaRepository<Imagen, Long> {
    /**
     * Encuentra todas las imágenes asociadas a un producto por su ID.
     * @param productoId ID del producto
     * @return Lista de imágenes
     */
    List<Imagen> findByProductoId(Long productoId);

    /**
     * Elimina todas las imágenes asociadas a un producto por su ID.
     * @param productoId ID del producto
     */
    void deleteByProductoId(Long productoId);
}