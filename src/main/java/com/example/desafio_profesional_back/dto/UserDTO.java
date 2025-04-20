package com.example.desafio_profesional_back.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private String role;
}
