package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByProductoIdAndEstado(Long productoId, String estado);

    List<Reserva> findByProductoIdAndFechaInicioGreaterThanEqualAndFechaFinLessThanEqual(
            Long productoId, LocalDate startDate, LocalDate endDate);
}