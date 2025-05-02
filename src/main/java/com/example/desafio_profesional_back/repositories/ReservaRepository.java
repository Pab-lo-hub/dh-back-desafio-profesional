package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.Reserva;
import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository is an interface that provides access to data in a database
 * Repositorio para operaciones CRUD con la entidad Reserva.
 */
@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    /**
     * Finds reservations by product ID and status.
     * @param productoId The product ID
     * @param estado The reservation status
     * @return List of matching reservations
     */
    List<Reserva> findByProductoIdAndEstado(Long productoId, String estado);

    /**
     * Finds reservations by product ID within a date range.
     * @param productoId The product ID
     * @return List of matching reservations
     */
    List<Reserva> findByProductoIdAndFechaInicioGreaterThanEqualAndFechaFinLessThanEqual(
            Long productoId, LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Checks if a reservation exists for a user and product.
     * @param usuario The user
     * @param producto The product
     * @return true if exists, false otherwise
     */
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Reserva r WHERE r.usuario = :usuario AND r.producto = :producto")
    boolean existsByUserAndProducto(@Param("usuario") User usuario, @Param("producto") Producto producto);

    /**
     * Finds reservations by product ID and user ID.
     * @param productoId The product ID
     * @param usuarioId The user ID
     * @return List of matching reservations
     */
    List<Reserva> findByProductoIdAndUsuarioId(Long productoId, Long usuarioId);

    /**
     * Finds reservations by user ID.
     * @param usuarioId The user ID
     * @return List of matching reservations
     */
    List<Reserva> findByUsuarioId(Long usuarioId);
}