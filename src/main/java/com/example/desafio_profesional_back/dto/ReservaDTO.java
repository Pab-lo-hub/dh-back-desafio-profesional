package com.example.desafio_profesional_back.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO for handling reservation data transfer
 */
@Getter
@Setter
public class ReservaDTO {
    private Long id;
    private Long productoId;
    private String productoNombre;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
}