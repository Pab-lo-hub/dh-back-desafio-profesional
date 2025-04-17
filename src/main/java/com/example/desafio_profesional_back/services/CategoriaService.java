package com.example.desafio_profesional_back.services;

import com.example.desafio_profesional_back.models.Categoria;
import com.example.desafio_profesional_back.repositories.CategoriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaService {

    @Autowired
    private final CategoriaRepository categoriaRepository;

    // Directorio para imágenes de categorías
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/categorias/";

    /**
     * Obtiene todas las categorías.
     * @return Lista de categorías.
     */
    public List<Categoria> getAllCategorias() {
        return categoriaRepository.findAll();
    }

    /**
     * Crea una nueva categoría con imagen opcional.
     * @param categoria Objeto categoría.
     * @param imagen Archivo de imagen (puede ser null).
     * @return Categoría creada.
     * @throws IOException Si falla el almacenamiento de la imagen.
     */
    public Categoria createCategoria(Categoria categoria, MultipartFile imagen) throws IOException {
        if (imagen != null && !imagen.isEmpty()) {
            String fileName = UUID.randomUUID() + "-" + imagen.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR, fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, imagen.getBytes());
            categoria.setImagen("/uploads/categorias/" + fileName);
        }
        Categoria savedCategoria = categoriaRepository.save(categoria);
        log.info("Categoría con id: {} creada", savedCategoria.getId());
        return savedCategoria;
    }

    /**
     * Elimina una categoría por ID.
     * @param id ID de la categoría.
     * @throws IllegalArgumentException Si la categoría no existe.
     */
    public void deleteCategoria(Long id) {
        if (!categoriaRepository.existsById(id)) {
            throw new IllegalArgumentException("Categoría no encontrada");
        }
        categoriaRepository.deleteById(id);
        log.info("Categoría con id: {} eliminada", id);
    }
}