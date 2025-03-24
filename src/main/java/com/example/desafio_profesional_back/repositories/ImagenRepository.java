package com.example.desafio_profesional_back.repositories;
import com.example.desafio_profesional_back.models.Imagen;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImagenRepository extends JpaRepository<Imagen, Integer> {
}