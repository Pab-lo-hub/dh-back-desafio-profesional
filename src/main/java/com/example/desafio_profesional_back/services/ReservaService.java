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
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ProductoRepository productoRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public ReservaDTO createReserva(ReservaDTO reservaDTO) throws MessagingException {
        log.debug("Creando reserva con DTO: {}", reservaDTO);

        if (reservaDTO.getUserId() == null) {
            log.error("El userId es nulo");
            throw new IllegalArgumentException("El userId no puede ser nulo");
        }
        if (reservaDTO.getProductId() == null) {
            log.error("El productId es nulo");
            throw new IllegalArgumentException("El productId no puede ser nulo");
        }

        Optional<Producto> productoOpt = productoRepository.findById(reservaDTO.getProductId());
        Optional<User> usuarioOpt = userRepository.findById(reservaDTO.getUserId());

        if (!productoOpt.isPresent() || !usuarioOpt.isPresent()) {
            log.error("Producto o usuario no encontrado: productId={}, userId={}",
                    reservaDTO.getProductId(), reservaDTO.getUserId());
            throw new IllegalArgumentException("Producto o usuario no encontrado");
        }

        LocalDate fechaInicio = reservaDTO.getStartDate();
        LocalDate fechaFin = reservaDTO.getEndDate();

        if (fechaInicio == null || fechaFin == null || fechaInicio.isAfter(fechaFin) || fechaInicio.isBefore(LocalDate.now())) {
            log.error("Fechas inválidas: startDate={}, endDate={}", fechaInicio, fechaFin);
            throw new IllegalArgumentException("Fechas inválidas");
        }

        List<Reserva> reservas = reservaRepository.findByProductoIdAndFechaInicioGreaterThanEqualAndFechaFinLessThanEqual(
                reservaDTO.getProductId(), fechaInicio, fechaFin);
        if (!reservas.isEmpty()) {
            log.error("Producto no disponible para las fechas: productId={}, startDate={}, endDate={}",
                    reservaDTO.getProductId(), fechaInicio, fechaFin);
            throw new IllegalStateException("El producto no está disponible en las fechas seleccionadas");
        }

        Reserva reserva = new Reserva();
        reserva.setProducto(productoOpt.get());
        reserva.setUsuario(usuarioOpt.get());
        reserva.setFechaInicio(fechaInicio);
        reserva.setFechaFin(fechaFin);
        reserva.setEstado("PENDIENTE");

        log.debug("Guardando reserva: {}", reserva);
        Reserva saved = reservaRepository.save(reserva);

        ReservaDTO result = new ReservaDTO();
        result.setId(saved.getId());
        result.setProductId(saved.getProducto().getId());
        result.setUserId(saved.getUsuario().getId());
        result.setStartDate(saved.getFechaInicio());
        result.setEndDate(saved.getFechaFin());
        result.setEstado(saved.getEstado());
        result.setProductoNombre(saved.getProducto().getNombre());
        result.setUsuarioNombre(saved.getUsuario().getNombre());

        try {
            emailService.sendReservationEmail(result, usuarioOpt.get().getEmail());
            log.info("Correo de confirmación enviado para la reserva ID: {}", saved.getId());
        } catch (MessagingException e) {
            log.error("Error al enviar correo para la reserva ID: {}, pero la reserva se creó correctamente", saved.getId(), e);
        }

        log.info("Reserva creada con ID: {}", saved.getId());
        return result;
    }

    public List<ReservaDTO> findByUsuarioId(Long usuarioId) {
        log.debug("Buscando reservas para usuario ID: {}", usuarioId);
        List<Reserva> reservas = reservaRepository.findByUsuarioId(usuarioId);
        return reservas.stream().map(reserva -> {
            ReservaDTO dto = new ReservaDTO();
            dto.setId(reserva.getId());
            dto.setProductId(reserva.getProducto().getId());
            dto.setProductoNombre(reserva.getProducto().getNombre());
            dto.setUserId(reserva.getUsuario().getId());
            dto.setUsuarioNombre(reserva.getUsuario().getNombre());
            dto.setStartDate(reserva.getFechaInicio());
            dto.setEndDate(reserva.getFechaFin());
            dto.setEstado(reserva.getEstado());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<ReservaDTO> findByProductoId(Long productoId) {
        log.debug("Buscando reservas para producto ID: {}", productoId);
        List<Reserva> reservas = reservaRepository.findByProductoIdAndEstado(productoId, "PENDIENTE");
        return reservas.stream().map(reserva -> {
            ReservaDTO dto = new ReservaDTO();
            dto.setId(reserva.getId());
            dto.setProductId(reserva.getProducto().getId());
            dto.setProductoNombre(reserva.getProducto().getNombre());
            dto.setUserId(reserva.getUsuario().getId());
            dto.setUsuarioNombre(reserva.getUsuario().getNombre());
            dto.setStartDate(reserva.getFechaInicio());
            dto.setEndDate(reserva.getFechaFin());
            dto.setEstado(reserva.getEstado());
            return dto;
        }).collect(Collectors.toList());
    }

    public ReservaDTO findById(Long id) {
        log.debug("Buscando reserva con ID: {}", id);
        Optional<Reserva> reservaOpt = reservaRepository.findById(id);
        if (!reservaOpt.isPresent()) {
            log.error("Reserva no encontrada: id={}", id);
            throw new IllegalArgumentException("Reserva no encontrada");
        }
        Reserva reserva = reservaOpt.get();
        ReservaDTO dto = new ReservaDTO();
        dto.setId(reserva.getId());
        dto.setProductId(reserva.getProducto().getId());
        dto.setProductoNombre(reserva.getProducto().getNombre());
        dto.setUserId(reserva.getUsuario().getId());
        dto.setUsuarioNombre(reserva.getUsuario().getNombre());
        dto.setStartDate(reserva.getFechaInicio());
        dto.setEndDate(reserva.getFechaFin());
        dto.setEstado(reserva.getEstado());
        return dto;
    }
}
