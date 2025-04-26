package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.Reserva;
import com.example.desafio_profesional_back.models.User;
import com.example.desafio_profesional_back.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByProductoIdAndEstado(Long productoId, String estado);

    List<Reserva> findByProductoIdAndFechaInicioGreaterThanEqualAndFechaFinLessThanEqual(
            Long productoId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END " +
            "FROM Reserva r WHERE r.user = :user AND r.producto = :producto")
    boolean existsByUserAndProducto(@Param("user") User user, @Param("producto") Producto producto);
}