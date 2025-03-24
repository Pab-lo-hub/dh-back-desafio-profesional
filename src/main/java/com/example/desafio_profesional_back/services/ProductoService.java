package com.example.desafio_profesional_back.services;

import com.example.desafio_profesional_back.models.Imagen;
import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.repositories.ImagenRepository;
import com.example.desafio_profesional_back.repositories.ProductoRepository;
import com.example.desafio_profesional_back.services.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Service layer is where all the business logic lies
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {
    @Autowired
    private final ProductoRepository productoRepository;

    @Autowired
    private ImagenRepository imagenRepository;

    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    public List<Producto> findAll() {
        return productoRepository.findAll();
    }

    public Producto getProductoById(Integer id) {
        Optional<Producto> optionalProducto = productoRepository.findById(id);
        if (optionalProducto.isPresent()) {
            return optionalProducto.get();
        }
        log.info("Producto con id: {} no existe", id);
        return null;
    }

    public Producto saveProducto(Producto producto, List<MultipartFile> imagenes) throws IOException {
        Producto savedProducto = productoRepository.save(producto);

        log.info("Producto con id: {} guardado exitosamente", producto.getId());
        // Guardar cada imagen
        if (imagenes != null && !imagenes.isEmpty()) {
            for (MultipartFile imagen : imagenes) {
                if (!imagen.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "-" + imagen.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR + fileName);
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

    public Producto update(Integer id, Producto productoDetails, List<MultipartFile> nuevasImagenes) throws IOException {
        Producto producto = findById(id);
        if (producto == null) return null;

        producto.setNombre(productoDetails.getNombre());
        producto.setDescripcion(productoDetails.getDescripcion());

        if (nuevasImagenes != null && !nuevasImagenes.isEmpty()) {
            for (MultipartFile imagen : nuevasImagenes) {
                if (!imagen.isEmpty()) {
                    String fileName = UUID.randomUUID().toString() + "-" + imagen.getOriginalFilename();
                    Path filePath = Paths.get(UPLOAD_DIR + fileName);
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

    public void deleteProductoById(Integer id) {
        productoRepository.deleteById(id);
    }

    public Producto findById(Integer id) {
        return productoRepository.findById(id).orElse(null);
    }

}
