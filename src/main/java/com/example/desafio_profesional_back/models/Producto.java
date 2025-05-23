package com.example.desafio_profesional_back.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * An entity class represents a table in a relational database
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "productos")
public class Producto {
    /*@Id: Representa a un primary key de nuestra tabla */
    @Id
    /*@GeneratedValue: Representa a un campo autogereado (secuencial), equivalente a un campo identity de una sentencia SQL.*/
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String descripcion;

    @Column
    private String precio;

    @Column(length = 1000)
    private String politicas;

    @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Serializa esta parte de la relación
    private List<Imagen> imagenes = new ArrayList<>(); // Lista de imágenes asociadas

    // Relación muchos-a-uno con categoría
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // Relación muchos-a-muchos con Feature
    @ManyToMany
    @JoinTable(
            name = "producto_features",
            joinColumns = @JoinColumn(name = "producto_id"),
            inverseJoinColumns = @JoinColumn(name = "feature_id")
    )
    private Set<Feature> features;
}