package com.example.desafio_profesional_back.controllers;

import com.example.desafio_profesional_back.dto.ReservaDTO;
import com.example.desafio_profesional_back.services.ReservaService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestionar reservas.
 * Usa ReservaDTO para evitar problemas con proxies de Hibernate.
 */
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
@Validated
public class ReservaController {
    @Autowired
    private ReservaService reservaService;

    /**
     * Crea una nueva reserva.
     * @param reservaDTO Datos de la reserva
     * @return ReservaDTO creado o error
     */
    @PostMapping
    public ResponseEntity<?> createReserva(@RequestBody ReservaDTO reservaDTO) {
        try {
            ReservaDTO created = reservaService.createReserva(reservaDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (MessagingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Reserva creada, pero error al enviar correo de confirmación: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear reserva: " + e.getMessage());
        }
    }

    /**
     * Obtiene las reservas de un usuario por su ID.
     * @param usuarioId ID del usuario
     * @return Lista de ReservaDTO
     */
    @GetMapping("/usuarios/{usuarioId}/reservas")
    public ResponseEntity<?> getReservasByUsuarioId(@PathVariable Long usuarioId) {
        try {
            List<ReservaDTO> reservas = reservaService.findByUsuarioId(usuarioId);
            return ResponseEntity.ok(reservas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cargar las reservas: " + e.getMessage());
        }
    }
}