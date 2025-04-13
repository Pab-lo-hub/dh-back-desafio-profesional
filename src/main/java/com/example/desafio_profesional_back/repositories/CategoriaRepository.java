package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//marcar una clase como un componente de acceso a datos
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Integer> {
    // Encuentra una categoría por su título
    Categoria findByTituloIgnoreCase(String titulo);
}
