package com.example.desafio_profesional_back.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * An entity class represents a table in a relational database
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "productos")
public class Producto {
    /*@Id: Representa a un primary key (llama primario) de nuestra tabla tbproduct*/
    @Id
    /*@GeneratedValue: Representa a un campo autogereado (secuencial), equivalente a un campo identity de una sentencia SQL.*/
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String nombre;
    private String descripcion;

}