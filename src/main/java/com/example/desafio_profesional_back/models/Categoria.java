package com.example.desafio_profesional_back.models;

import jakarta.persistence.*;
import lombok.Data;

// librería Lombok
@Data
// marcar una clase como una entidad de JPA (Java Persistence API). Esto significa que la clase está asociada a una tabla en la base de datos
@Entity

//especificar los detalles de la tabla en la base de datos con la que se mapeará una clase anotada como @Entity
@Table(name = "categorias")

public class Categoria {

    // Identificador único de la categoría
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Título de la categoría
    @Column(nullable = false, length = 100)
    private String titulo;

    // Descripción de la categoría
    @Column(length = 500)
    private String descripcion;

    // URL o ruta de la imagen de la categoría
    @Column(length = 255)
    private String imagen;
}
