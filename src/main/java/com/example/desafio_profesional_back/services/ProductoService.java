package com.example.desafio_profesional_back.services;

import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.repositories.ProductoRepo;
import com.example.desafio_profesional_back.services.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer is where all the business logic lies
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {
    private final ProductoRepo productoRepo;

    public List<Producto> findAll() {
        return productoRepo.findAll();
    }

    public Producto getProductoById(Integer id) {
        Optional<Producto> optionalProducto = productoRepo.findById(id);
        if (optionalProducto.isPresent()) {
            return optionalProducto.get();
        }
        log.info("Producto con id: {} no existe", id);
        return null;
    }

    public Producto saveProducto(Producto producto) {
        Producto savedProducto = productoRepo.save(producto);

        log.info("Producto con id: {} guardado exitosamente", producto.getId());
        return savedProducto;
    }

    public Producto updateProducto(Producto producto) {
        Optional<Producto> existingProducto = productoRepo.findById(producto.getId());


        Producto updatedProducto = productoRepo.save(producto);

        log.info("Producto con id: {} actualizado exitosamente", producto.getId());
        return updatedProducto;
    }

    public void deleteProductoById(Integer id) {
        productoRepo.deleteById(id);
    }

}
