package com.example.desafio_profesional_back.dto;

import lombok.Data;

@Data
public class FavoritoDTO {
    private Long id;
    private Long userId;
    private Long productId;
}
