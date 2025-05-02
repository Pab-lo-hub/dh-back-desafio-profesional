package com.example.desafio_profesional_back.services;

import com.example.desafio_profesional_back.dto.ReservaDTO;
import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.models.Reserva;
import com.example.desafio_profesional_back.models.User;
import com.example.desafio_profesional_back.repositories.ProductoRepository;
import com.example.desafio_profesional_back.repositories.ReservaRepository;
import com.example.desafio_profesional_back.repositories.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Capa de servicio que contiene la lógica de negocio para reservas.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaService {

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    /**
     * Crea una nueva reserva y envía un correo de confirmación al usuario.
     * @param reservaDTO Datos de la reserva
     * @return ReservaDTO creado
     * @throws IllegalArgumentException si los datos son inválidos o el producto no está disponible
     * @throws MessagingException si falla el envío del correo
     */
    public ReservaDTO createReserva(ReservaDTO reservaDTO) throws MessagingException {
        Optional<Producto> productoOpt = productoRepository.findById(reservaDTO.getProductoId());
        Optional<User> usuarioOpt = userRepository.findById(reservaDTO.getUsuarioId());

        if (!productoOpt.isPresent() || !usuarioOpt.isPresent()) {
            throw new IllegalArgumentException("Producto o usuario no encontrado");
        }

        LocalDate fechaInicio = reservaDTO.getFechaInicio();
        LocalDate fechaFin = reservaDTO.getFechaFin();

        if (fechaInicio == null || fechaFin == null || fechaInicio.isAfter(fechaFin) || fechaInicio.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Fechas inválidas");
        }

        List<Reserva> reservas = reservaRepository.findByProductoIdAndFechaInicioGreaterThanEqualAndFechaFinLessThanEqual(
                reservaDTO.getProductoId(), fechaInicio, fechaFin);
        if (!reservas.isEmpty()) {
            throw new IllegalStateException("El producto no está disponible en las fechas seleccionadas");
        }

        Reserva reserva = new Reserva();
        reserva.setProducto(productoOpt.get());
        reserva.setUsuario(usuarioOpt.get());
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        reserva.setEstado("PENDIENTE");

        reservaRepository.save(reserva);

        ReservaDTO result = new ReservaDTO();
        result.setId(reserva.getId());
        result.setProductoId(reserva.getProducto().getId());
        result.setUsuarioId(reserva.getUsuario().getId());
        result.setFechaInicio(reserva.getFechaInicio());
        result.setFechaFin(reserva.getFechaFin());
        result.setEstado(reserva.getEstado());

        // Enviar correo de confirmación
        try {
            emailService.sendReservationEmail(result, usuarioOpt.get().getEmail());
            log.info("Correo de confirmación enviado para la reserva ID: {}", reserva.getId());
        } catch (MessagingException e) {
            log.error("Error al enviar correo para la reserva ID: {}", reserva.getId(), e);
            throw e; // Propagar la excepción para que el controlador la maneje
        }

        log.info("Reserva creada con ID: {}", reserva.getId());
        return result;
    }

    /**
     * Obtiene las reservas de un usuario por su ID.
     * @param usuarioId ID del usuario
     * @return Lista de ReservaDTO
     */
    public List<ReservaDTO> findByUsuarioId(Long usuarioId) {
        List<Reserva> reservas = reservaRepository.findByUsuarioId(usuarioId);
        return reservas.stream().map(reserva -> {
            ReservaDTO dto = new ReservaDTO();
            dto.setId(reserva.getId());
            dto.setProductoId(reserva.getProducto().getId());
            dto.setProductoNombre(reserva.getProducto().getNombre());
            dto.setUsuarioId(reserva.getUsuario().getId());
            dto.setUsuarioNombre(reserva.getUsuario().getNombre());
            dto.setFechaInicio(reserva.getFechaInicio());
            dto.setFechaFin(reserva.getFechaFin());
            dto.setEstado(reserva.getEstado());
            return dto;
        }).collect(Collectors.toList());
    }
}