package com.example.desafio_profesional_back.dto;

import lombok.Data;

@Data
public class PuntuacionDTO {
    private Long id;
    private Long productoId;
    private Long usuarioId;
    private Integer estrellas;
}
