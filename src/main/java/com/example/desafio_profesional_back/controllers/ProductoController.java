package com.example.desafio_profesional_back.controllers;

import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
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

    // Crear un producto con múltiples imágenes
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Producto> createProducto(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("imagenes") List<MultipartFile> imagenes) throws IOException {
        Producto producto = new Producto();
        producto.setNombre(nombre);
        producto.setDescripcion(descripcion);

        Producto savedProducto = productoService.saveProducto(producto, imagenes);
        return new ResponseEntity<>(savedProducto, HttpStatus.CREATED);
    }

    // Obtener todos los productos
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProductos() {
        return ResponseEntity.ok(productoService.findAll());
    }

    // Obtener un producto por ID
    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProducto(@PathVariable Integer id) {
        Producto producto = productoService.findById(id);
        if (producto != null) {
            return ResponseEntity.ok(producto);
        }
        return ResponseEntity.notFound().build();
    }

    // Actualizar un producto con nuevas imágenes (opcional)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Producto> updateProducto(
            @PathVariable Integer id,
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam(value = "imagenes", required = false) List<MultipartFile> imagenes) throws IOException {
        Producto productoDetails = new Producto();
        productoDetails.setNombre(nombre);
        productoDetails.setDescripcion(descripcion);

        Producto updatedProducto = productoService.update(id, productoDetails, imagenes);
        if (updatedProducto != null) {
            return ResponseEntity.ok(updatedProducto);
        }
        return ResponseEntity.notFound().build();
    }

    // Eliminar un producto
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProducto(@PathVariable Integer id) {
        Producto producto = productoService.findById(id);
        if (producto == null) return ResponseEntity.notFound().build();
        productoService.deleteProductoById(id);
        return ResponseEntity.noContent().build();
    }

    // Servir una imagen
    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get("src/main/resources/static/uploads/" + filename);
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists() && resource.isReadable()) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }
}
