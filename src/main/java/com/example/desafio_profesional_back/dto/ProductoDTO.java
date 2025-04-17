package com.example.desafio_profesional_back.dto;


import lombok.Data;

import java.util.List;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private CategoriaDTO categoria;
    private List<ImagenDTO> imagenes;

    @Data
    public static class CategoriaDTO {
        private Long id;
        private String titulo;
    }

    @Data
    public static class ImagenDTO {
        private Long id;
        private String ruta;
    }
}