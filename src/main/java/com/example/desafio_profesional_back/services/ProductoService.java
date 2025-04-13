// src/main/java/com/example/desafio_profesional_back/services/ProductoService.java
package com.example.desafio_profesional_back.services;

import com.example.desafio_profesional_back.models.Categoria;
import com.example.desafio_profesional_back.models.Imagen;
import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.repositories.CategoriaRepository;
import com.example.desafio_profesional_back.repositories.ImagenRepository;
import com.example.desafio_profesional_back.repositories.ProductoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Capa de servicio que contiene la lógica de negocio para productos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    @Autowired
    private final ProductoRepository productoRepository;

    @Autowired
    private final ImagenRepository imagenRepository;

    @Autowired
    private final CategoriaRepository categoriaRepository;

    // Directorio para almacenar imágenes
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    /**
     * Obtiene todos los productos.
     * @return Lista de productos.
     */
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    /**
     * Obtiene un producto por su ID.
     * @param id ID del producto.
     * @return Producto encontrado o null si no existe.
     */
    public Producto getProductoById(Integer id) {
        Optional<Producto> optionalProducto = productoRepository.findById(id);
        if (optionalProducto.isPresent()) {
            return optionalProducto.get();
        }
        log.info("Producto con id: {} no existe", id);
        return null;
    }

    /**
     * Guarda un nuevo producto con imágenes y categoría opcional.
     * @param producto Objeto producto con nombre y descripción.
     * @param imagenes Lista de imágenes (puede ser vacía).
     * @param categoriaId ID de la categoría (puede ser null).
     * @return Producto guardado.
     * @throws IOException Si falla el almacenamiento de imágenes.
     * @throws IllegalArgumentException Si la categoría no existe.
     */
    public Producto saveProducto(Producto producto, List<MultipartFile> imagenes, Integer categoriaId) throws IOException {
        // Asigna la categoría si se proporciona
        if (categoriaId != null) {
            Categoria categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        }

        // Guarda el producto
        Producto savedProducto = productoRepository.save(producto);
        log.info("Producto con id: {} guardado exitosamente", savedProducto.getId());

        // Guarda las imágenes
        if (imagenes != null && !imagenes.isEmpty()) {
            for (MultipartFile imagen : imagenes) {
                if (!imagen.isEmpty()) {
                    String fileName = UUID.randomUUID() + "-" + imagen.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR, fileName);
                    Files.createDirectories(filePath.getParent());
                    Files.write(filePath, imagen.getBytes());

                    Imagen nuevaImagen = new Imagen();
                    nuevaImagen.setRuta("/uploads/" + fileName);
                    nuevaImagen.setProducto(savedProducto);
                    imagenRepository.save(nuevaImagen);
                    savedProducto.getImagenes().add(nuevaImagen);
                }
            }
        }

        return savedProducto;
    }

    /**
     * Actualiza un producto existente con nueva información y/o imágenes.
     * @param id ID del producto.
     * @param productoDetails Nuevos detalles del producto.
     * @param nuevasImagenes Nuevas imágenes (puede ser vacía).
     * @param categoriaId ID de la categoría (puede ser null).
     * @return Producto actualizado o null si no existe.
     * @throws IOException Si falla el almacenamiento de imágenes.
     */
    public Producto update(Integer id, Producto productoDetails, List<MultipartFile> nuevasImagenes, Integer categoriaId) throws IOException {
        Producto producto = findById(id);
        if (producto == null) {
            log.info("Producto con id: {} no existe", id);
            return null;
        }

        // Actualiza nombre y descripción
        producto.setNombre(productoDetails.getNombre());
        producto.setDescripcion(productoDetails.getDescripcion());

        // Actualiza categoría
        if (categoriaId != null) {
            Categoria categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            producto.setCategoria(categoria);
        } else {
            producto.setCategoria(null);
        }

        // Actualiza imágenes
        if (nuevasImagenes != null && !nuevasImagenes.isEmpty()) {
            for (MultipartFile imagen : nuevasImagenes) {
                if (!imagen.isEmpty()) {
                    String fileName = UUID.randomUUID() + "-" + imagen.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR, fileName);
                    Files.write(filePath, imagen.getBytes());

                    Imagen nuevaImagen = new Imagen();
                    nuevaImagen.setRuta("/uploads/" + fileName);
                    nuevaImagen.setProducto(producto);
                    imagenRepository.save(nuevaImagen);
                    producto.getImagenes().add(nuevaImagen);
                }
            }
        }

        return productoRepository.save(producto);
    }

    /**
     * Elimina un producto por su ID.
     * @param id ID del producto.
     */
    public void deleteProductoById(Integer id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            log.info("Producto con id: {} eliminado", id);
        } else {
            log.info("Producto con id: {} no existe", id);
        }
    }

    /**
     * Busca un producto por su ID.
     * @param id ID del producto.
     * @return Producto encontrado o null.
     */
    public Producto findById(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

    /**
     * Verifica si existe un producto por su nombre.
     * @param nombre Nombre del producto.
     * @return true si existe, false si no.
     */
    public boolean existsByNombre(String nombre) {
        return productoRepository.existsByNombre(nombre);
    }

    /**
     * Asigna una categoría a un producto existente.
     * @param productoId ID del producto.
     * @param categoriaId ID de la categoría.
     * @return Producto actualizado.
     * @throws IllegalArgumentException Si el producto o categoría no existen.
     */
    public Producto asignarCategoria(Integer productoId, Integer categoriaId) {
        Producto producto = findById(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no encontrado");
        }
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
        producto.setCategoria(categoria);
        return productoRepository.save(producto);
    }
}