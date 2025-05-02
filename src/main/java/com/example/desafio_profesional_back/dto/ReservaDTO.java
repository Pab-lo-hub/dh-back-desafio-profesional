package com.example.desafio_profesional_back.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * DTO for handling reservation data transfer
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReservaDTO {
    private Long id;
    private Long userId;
    private Long productId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String estado;
    private String productoNombre;
    private String usuarioNombre;
}