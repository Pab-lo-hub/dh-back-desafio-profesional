package com.example.desafio_profesional_back.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

/**
 * An entity class represents a table in a relational database
 */
@Entity
@Table(name = "puntuaciones")
@Getter
@Setter
public class Puntuacion {
    /*@Id: Representa a un primary key de nuestra tabla */
    @Id
    /*@GeneratedValue: Representa a un campo autogereado (secuencial), equivalente a un campo identity de una sentencia SQL.*/
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int estrellas;
}