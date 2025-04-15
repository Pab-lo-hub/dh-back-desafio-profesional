package com.example.desafio_profesional_back.controllers;

import com.example.desafio_profesional_back.models.Categoria;
import com.example.desafio_profesional_back.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para gestionar categorías.
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaRepository categoriaRepository;

    /**
     * Obtiene todas las categorías disponibles.
     * @return Lista de categorías
     */
    @GetMapping
    public ResponseEntity<List<Categoria>> getAllCategorias() {
        List<Categoria> categorias = categoriaRepository.findAll();
        return ResponseEntity.ok(categorias);
    }

    /**
     * Crea una nueva categoría.
     * @param categoria Datos de la categoría
     * @return Categoría creada
     */
    @PostMapping
    public ResponseEntity<Categoria> createCategoria(@RequestBody Categoria categoria) {
        Categoria saved = categoriaRepository.save(categoria);
        return ResponseEntity.ok(saved);
    }
}
