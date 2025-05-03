package com.example.desafio_profesional_back.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    @JsonProperty("price")
    private String precio;
    private Long categoria_id;
    private CategoriaDTO categoria;
    private List<ImagenDTO> imagenes;
    private Set<FeatureDTO> features;
    private String politicas;

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