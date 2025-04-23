package com.example.desafio_profesional_back.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class AvailabilityDTO {
    private Long id;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estado;
}