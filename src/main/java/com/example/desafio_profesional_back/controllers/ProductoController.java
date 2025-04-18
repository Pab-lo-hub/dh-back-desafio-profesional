package com.example.desafio_profesional_back.controllers;

import com.example.desafio_profesional_back.dto.ProductoDTO;
import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.services.ProductoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Controlador para gestionar productos.
 * Usa ProductoDTO para evitar problemas con proxies de Hibernate.
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Validated
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Obtiene todos los productos como DTOs.
     * @return Lista de ProductoDTO o error
     */
    @GetMapping
    public ResponseEntity<?> getAllProductos() {
        try {
            List<ProductoDTO> productos = productoService.findAll();
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cargar los productos: " + e.getMessage());
        }
    }

    /**
     * Obtiene un producto por su ID.
     * @param id ID del producto
     * @return ProductoDTO o error
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductoById(@PathVariable Long id) {
        try {
            ProductoDTO producto = productoService.findById(id);
            if (producto == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Producto no encontrado");
            }
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cargar el producto: " + e.getMessage());
        }
    }

    /**
     * Parsea categoriaId desde un string, permitiendo null.
     * @param categoriaIdStr String con el ID de la categoría
     * @return Long o null
     */
    private Long parseCategoriaId(String categoriaIdStr) {
        if (categoriaIdStr == null || categoriaIdStr.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(categoriaIdStr);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ID de categoría inválido");
        }
    }

    /**
     * Actualiza un producto existente.
     * @param id ID del producto
     * @param productoJson JSON con datos del producto (nombre, descripción)
     * @param imagenes Nuevas imágenes (opcional)
     * @param categoriaIdStr ID de la categoría (opcional)
     * @return ProductoDTO actualizado o error
     */
    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateProducto(
            @PathVariable Long id,
            @RequestPart("producto") String productoJson,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
            @RequestParam(value = "categoriaId", required = false) String categoriaIdStr) {
        try {
            ProductoDTO productoDTO = objectMapper.readValue(productoJson, ProductoDTO.class);
            Long categoriaId = parseCategoriaId(categoriaIdStr);
            ProductoDTO updated = productoService.update(id, productoDTO, imagenes, categoriaId);
            if (updated == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar los datos");
        }
    }

    /**
     * Crea un nuevo producto.
     * @param productoJson JSON con datos del producto
     * @param imagenes Imágenes (opcional)
     * @param categoriaIdStr ID de la categoría (opcional)
     * @return ProductoDTO creado
     */
    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createProducto(
            @RequestPart("producto") String productoJson,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
            @RequestParam(value = "categoriaId", required = false) String categoriaIdStr) {
        try {
            ProductoDTO productoDTO = objectMapper.readValue(productoJson, ProductoDTO.class);
            Long categoriaId = parseCategoriaId(categoriaIdStr);
            ProductoDTO created = productoService.create(productoDTO, imagenes, categoriaId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al procesar los datos");
        }
    }

    /**
     * Elimina un producto por su ID.
     * @param id ID del producto
     * @return Respuesta vacía o error
     */
    @DeleteMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteProducto(@PathVariable Long id) {
        try {
            boolean deleted = productoService.delete(id);
            if (!deleted) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Producto no encontrado");
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al eliminar el producto: " + e.getMessage());
        }
    }
}