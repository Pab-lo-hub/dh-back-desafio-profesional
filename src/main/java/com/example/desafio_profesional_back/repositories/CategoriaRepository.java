package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//Anotación para marcar una clase como un componente de acceso a datos
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    // Encuentra una categoría por su título
    Categoria findByTituloIgnoreCase(String titulo);
}
