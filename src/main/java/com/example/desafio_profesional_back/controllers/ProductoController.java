package com.example.desafio_profesional_back.controllers;

import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.services.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/producto/v1")
@RequiredArgsConstructor
@Validated
public class ProductoController {
    private final ProductoService productoService;

    /**
     * This method is called when a GET request is made
     * URL: localhost:8080/producto/v1/
     */
    @GetMapping("/")
    public ResponseEntity<List<Producto>> getAllProductos() {
        return ResponseEntity.ok().body(new ArrayList<Producto>());
    }

    /**
     * This method is called when a GET request is made
     * URL: localhost:8080/product/v1/1 (or any other id)
     * Purpose: Fetches product with the given id
     * @param id - product id
     * @return Product with the given id
     */

    @GetMapping("/{id}")
    public ResponseEntity<Producto> getProductoById(@PathVariable Integer id) {
        return ResponseEntity.ok().body(productoService.getProductoById(id));
    }

    /**
     * This method is called when a POST request is made
     * URL: localhost:8080/producto/v1/
     * Purpose: Save a Producto entity
     * @param producto - Request body is an Product entity
     * @return Saved Producto entity
     */
    @PostMapping("/")
    public ResponseEntity<Producto> saveProducto(@RequestBody Producto producto){
        return ResponseEntity.ok().body(productoService.saveProducto(producto));
    }

    /**
     * This method is called when a PUT request is made
     * URL: localhost:8080/producto/v1/
     * Purpose: Update a Producto entity
     * @param producto - Producto entity to be updated
     * @return Updated Producto
     */
    @PutMapping("/")
    public ResponseEntity<Producto> updateProducto (@RequestBody Producto producto){
        return ResponseEntity.ok().body(productoService.updateProducto(producto));
    }

    /**
     * This method is called when a Delete request is made
     * URL: localhost:8080/producto/v1/1 (or any other id)
     * Purpose: Delete an Producto entity
     * @param id - Producto's id to be deleted
     * @return a String message indicating producto record has been deleted successfully
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProducto(@PathVariable Integer id) {
        productoService.deleteProductoById(id);
        return ResponseEntity.ok().body("Eliminado correctamente");
    }


}
