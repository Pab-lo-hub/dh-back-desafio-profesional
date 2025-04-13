package com.example.desafio_profesional_back.controllers;

import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Validated
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    /**
     * Obtiene todos los productos.
     * @return Lista de productos.
     */
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    /**
     * Obtiene un producto por ID.
     * @param id ID del producto.
     * @return Producto encontrado.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Integer id) {
        Producto producto = productoService.getProductoById(id);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(producto);
    }

    /**
     * Crea un nuevo producto con imágenes y categoría opcional.
     * @param producto Objeto producto.
     * @param imagenes Lista de imágenes.
     * @param categoriaId ID de la categoría (opcional).
     * @return Producto creado.
     * @throws IOException Si falla el almacenamiento de imágenes.
     */
    @PostMapping
    public ResponseEntity<Producto> createProducto(
            @RequestPart("producto") Producto producto,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
            @RequestParam(value = "categoriaId", required = false) Integer categoriaId) throws IOException {
        Producto created = productoService.saveProducto(producto, imagenes, categoriaId);
        return ResponseEntity.ok(created);
    }

    /**
     * Asigna una categoría a un producto existente.
     * @param id ID del producto.
     * @param categoriaId ID de la categoría.
     * @return Producto actualizado.
     */
    @PutMapping("/{id}/categoria")
    public ResponseEntity<Producto> asignarCategoria(
            @PathVariable Integer id,
            @RequestParam Integer categoriaId) {
        return ResponseEntity.ok(productoService.asignarCategoria(id, categoriaId));
    }

    /**
     * Actualiza un producto existente.
     * @param id ID del producto.
     * @param producto Objeto producto con nuevos datos.
     * @param imagenes Nuevas imágenes (opcional).
     * @param categoriaId ID de la categoría (opcional).
     * @return Producto actualizado.
     * @throws IOException Si falla el almacenamiento de imágenes.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Producto> updateProducto(
            @PathVariable Integer id,
            @RequestPart("producto") Producto producto,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
            @RequestParam(value = "categoriaId", required = false) Integer categoriaId) throws IOException {
        Producto updated = productoService.update(id, producto, imagenes, categoriaId);
        if (updated == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updated);
    }

    /**
     * Elimina un producto por ID.
     * @param id ID del producto.
     * @return Respuesta vacía si se elimina con éxito.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Integer id) {
        productoService.deleteProductoById(id);
        return ResponseEntity.noContent().build();
    }
}