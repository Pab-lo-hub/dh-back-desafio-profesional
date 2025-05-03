package com.example.desafio_profesional_back.controllers;

import com.example.desafio_profesional_back.dto.AvailabilityDTO;
import com.example.desafio_profesional_back.dto.PoliticaDTO;
import com.example.desafio_profesional_back.dto.ProductoDTO;
import com.example.desafio_profesional_back.dto.PuntuacionDTO;
import com.example.desafio_profesional_back.services.ProductoService;
import com.example.desafio_profesional_back.services.PuntuacionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
    private PuntuacionService puntuacionService;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Obtiene todos los productos como DTOs, con soporte para filtrado por categorías.
     * @param categoriaIdStr IDs de categorías separados por comas (opcional)
     * @return Lista de ProductoDTO o error
     */
    @GetMapping
    public ResponseEntity<?> getAllProductos(
            @RequestParam(value = "categoria_id", required = false) String categoriaIdStr) {
        try {
            List<ProductoDTO> productos;
            if (categoriaIdStr != null && !categoriaIdStr.isEmpty()) {
                List<Long> categoriaIds = Arrays.stream(categoriaIdStr.split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                productos = productoService.findByCategoriaIds(categoriaIds);
            } else {
                productos = productoService.findAll();
            }
            return ResponseEntity.ok(productos);
        } catch (NumberFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("ID de categoría inválido: " + e.getMessage());
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
     * Obtiene las políticas de un producto por su ID.
     * @param id ID del producto
     * @return Lista de PoliticaDTO o error
     */
    @GetMapping("/{id}/politicas")
    public ResponseEntity<?> getPoliticasByProductoId(@PathVariable Long id) {
        try {
            List<PoliticaDTO> politicas = productoService.getPoliticasByProductoId(id);
            return ResponseEntity.ok(politicas);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cargar las políticas: " + e.getMessage());
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
     * @param featureIds IDs de las características (opcional)
     * @return ProductoDTO actualizado o error
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProducto(
            @PathVariable Long id,
            @RequestPart("producto") String productoJson,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
            @RequestParam(value = "categoriaId", required = false) String categoriaIdStr,
            @RequestParam(value = "featureIds", required = false) List<Long> featureIds) {
        try {
            ProductoDTO productoDTO = objectMapper.readValue(productoJson, ProductoDTO.class);
            Long categoriaId = parseCategoriaId(categoriaIdStr);
            ProductoDTO updated = productoService.update(id, productoDTO, imagenes, categoriaId, featureIds);
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
     * @param featureIds IDs de las características (opcional)
     * @return ProductoDTO creado
     */
    @PostMapping
    public ResponseEntity<?> createProducto(
            @RequestPart("producto") String productoJson,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes,
            @RequestParam(value = "categoriaId", required = false) String categoriaIdStr,
            @RequestParam(value = "featureIds", required = false) List<Long> featureIds) {
        try {
            ProductoDTO productoDTO = objectMapper.readValue(productoJson, ProductoDTO.class);
            Long categoriaId = parseCategoriaId(categoriaIdStr);
            ProductoDTO created = productoService.create(productoDTO, imagenes, categoriaId, featureIds);
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

    /**
     * Busca productos por nombre y disponibilidad.
     * @param query Nombre del producto (opcional)
     * @param startDate Fecha de inicio (opcional)
     * @param endDate Fecha de fin (opcional)
     * @return Lista de ProductoDTO
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductoDTO>> searchProducts(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<ProductoDTO> results = productoService.searchProducts(query, startDate, endDate);
        return ResponseEntity.ok(results);
    }

    /**
     * Obtiene sugerencias de productos basadas en el nombre.
     * @param query Nombre parcial del producto
     * @return Lista de nombres de productos
     */
    @GetMapping("/suggest")
    public ResponseEntity<List<String>> getProductSuggestions(@RequestParam String query) {
        List<String> suggestions = productoService.getProductSuggestions(query);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Obtiene la disponibilidad de un producto.
     * @param id ID del producto
     * @return Lista de AvailabilityDTO
     */
    @GetMapping("/{id}/availability")
    public ResponseEntity<List<AvailabilityDTO>> getProductAvailability(@PathVariable Long id) {
        List<AvailabilityDTO> availability = productoService.getProductAvailability(id);
        return ResponseEntity.ok(availability);
    }

    /**
     * Verifica si un usuario puede puntuar un producto.
     * @param id ID del producto
     * @param usuarioId ID del usuario
     * @return true si puede puntuar, false en caso contrario
     */
    @GetMapping("/{id}/can-rate")
    public ResponseEntity<?> canUserRateProducto(
            @PathVariable Long id,
            @RequestParam Long usuarioId) {
        try {
            boolean canRate = productoService.canUserRateProducto(id, usuarioId);
            return ResponseEntity.ok(canRate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al verificar puntuación: " + e.getMessage());
        }
    }

    /**
     * Crea una nueva puntuación para un producto.
     * @param id ID del producto
     * @param puntuacionDTO Datos de la puntuación
     * @return PuntuacionDTO creado
     */
    @PostMapping("/{id}/puntuaciones")
    public ResponseEntity<?> createPuntuacion(
            @PathVariable Long id,
            @RequestBody PuntuacionDTO puntuacionDTO) {
        try {
            puntuacionDTO.setProductoId(id);
            PuntuacionDTO created = puntuacionService.addPuntuacion(puntuacionDTO);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear puntuación: " + e.getMessage());
        }
    }

    /**
     * Obtiene las puntuaciones de un producto.
     * @param productoId ID del producto
     * @return Lista de PuntuacionDTO
     */
    @GetMapping("/{productoId}/puntuaciones")
    public ResponseEntity<?> getPuntuaciones(@PathVariable Long productoId) {
        try {
            List<PuntuacionDTO> puntuaciones = productoService.getPuntuacionesByProductoId(productoId);
            return ResponseEntity.ok(puntuaciones);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cargar puntuaciones: " + e.getMessage());
        }
    }
}