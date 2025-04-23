package com.example.desafio_profesional_back.services;

import com.example.desafio_profesional_back.dto.AvailabilityDTO;
import com.example.desafio_profesional_back.dto.ProductoDTO;
import com.example.desafio_profesional_back.dto.FeatureDTO;
import com.example.desafio_profesional_back.models.*;
import com.example.desafio_profesional_back.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Capa de servicio que contiene la lógica de negocio para productos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ImagenRepository imagenRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    /**
     * Obtiene todos los productos como DTOs.
     * @return Lista de ProductoDTO
     */
    public List<ProductoDTO> findAll() {
        List<Producto> productos = productoRepository.findAll();
        List<ProductoDTO> result = new ArrayList<>();

        for (Producto producto : productos) {
            ProductoDTO dto = new ProductoDTO();
            dto.setId(producto.getId());
            dto.setNombre(producto.getNombre());
            dto.setDescripcion(producto.getDescripcion());

            // Mapear categoría
            if (producto.getCategoria() != null) {
                ProductoDTO.CategoriaDTO categoriaDTO = new ProductoDTO.CategoriaDTO();
                categoriaDTO.setId(producto.getCategoria().getId());
                categoriaDTO.setTitulo(producto.getCategoria().getTitulo());
                dto.setCategoria(categoriaDTO);
            }

            // Mapear imágenes
            List<Imagen> imagenes = imagenRepository.findByProductoId(producto.getId());
            List<ProductoDTO.ImagenDTO> imagenDTOs = imagenes.stream().map(imagen -> {
                ProductoDTO.ImagenDTO imagenDTO = new ProductoDTO.ImagenDTO();
                imagenDTO.setId(imagen.getId());
                imagenDTO.setRuta(imagen.getRuta());
                return imagenDTO;
            }).toList();
            dto.setImagenes(imagenDTOs);

            // Mapear características
            if (producto.getFeatures() != null) {
                Set<FeatureDTO> featureDTOs = producto.getFeatures().stream().map(feature -> {
                    FeatureDTO featureDTO = new FeatureDTO();
                    featureDTO.setId(feature.getId());
                    featureDTO.setNombre(feature.getNombre());
                    featureDTO.setIcono(feature.getIcono());
                    return featureDTO;
                }).collect(Collectors.toSet());
                dto.setFeatures(featureDTOs);
            }

            result.add(dto);
        }

        return result;
    }

    /**
     * Obtiene un producto por su ID como DTO.
     * @param id ID del producto
     * @return ProductoDTO o null si no existe
     */
    public ProductoDTO findById(Long id) {
        Optional<Producto> productoOpt = productoRepository.findById(id);
        if (!productoOpt.isPresent()) {
            log.warn("Producto no encontrado: {}", id);
            return null;
        }
        Producto producto = productoOpt.get();
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());

        // Mapear categoría
        if (producto.getCategoria() != null) {
            ProductoDTO.CategoriaDTO categoriaDTO = new ProductoDTO.CategoriaDTO();
            categoriaDTO.setId(producto.getCategoria().getId());
            categoriaDTO.setTitulo(producto.getCategoria().getTitulo());
            dto.setCategoria(categoriaDTO);
        }

        // Mapear imágenes
        List<Imagen> imagenes = imagenRepository.findByProductoId(producto.getId());
        List<ProductoDTO.ImagenDTO> imagenDTOs = imagenes.stream().map(imagen -> {
            ProductoDTO.ImagenDTO imagenDTO = new ProductoDTO.ImagenDTO();
            imagenDTO.setId(imagen.getId());
            imagenDTO.setRuta(imagen.getRuta());
            return imagenDTO;
        }).toList();
        dto.setImagenes(imagenDTOs);

        // Mapear características
        if (producto.getFeatures() != null) {
            Set<FeatureDTO> featureDTOs = producto.getFeatures().stream().map(feature -> {
                FeatureDTO featureDTO = new FeatureDTO();
                featureDTO.setId(feature.getId());
                featureDTO.setNombre(feature.getNombre());
                featureDTO.setIcono(feature.getIcono());
                return featureDTO;
            }).collect(Collectors.toSet());
            dto.setFeatures(featureDTOs);
        }

        return dto;
    }

    /**
     * Actualiza un producto existente usando datos de un DTO.
     * @param id ID del producto
     * @param productoDTO Datos actualizados (nombre, descripción)
     * @param imagenes Nuevas imágenes
     * @param categoriaId ID de la categoría
     * @param featureIds IDs de las características
     * @return ProductoDTO actualizado o null si no existe
     * @throws IOException si falla el manejo de imágenes
     */
    public ProductoDTO update(Long id, ProductoDTO productoDTO, List<MultipartFile> imagenes, Long categoriaId, List<Long> featureIds) throws IOException {
        Optional<Producto> existing = productoRepository.findById(id);
        if (!existing.isPresent()) {
            return null;
        }
        Producto toUpdate = existing.get();
        toUpdate.setNombre(productoDTO.getNombre());
        toUpdate.setDescripcion(productoDTO.getDescripcion());

        // Actualizar categoría
        if (categoriaId != null) {
            Optional<Categoria> categoria = categoriaRepository.findById(categoriaId);
            toUpdate.setCategoria(categoria.orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada")));
        } else {
            toUpdate.setCategoria(null);
        }

        // Actualizar características
        if (featureIds != null) {
            Set<Feature> features = featureIds.stream()
                    .map(fid -> featureRepository.findById(fid)
                            .orElseThrow(() -> new IllegalArgumentException("Característica no encontrada: " + fid)))
                    .collect(Collectors.toSet());
            toUpdate.setFeatures(features);
        } else {
            toUpdate.setFeatures(null);
        }

        // Guardar producto
        productoRepository.save(toUpdate);

        // Manejar imágenes
        if (imagenes != null && !imagenes.isEmpty()) {
            // Eliminar imágenes existentes
            imagenRepository.deleteByProductoId(id);
            // Guardar nuevas imágenes
            for (MultipartFile file : imagenes) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get("uploads/" + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, file.getBytes());
                Imagen imagen = new Imagen();
                imagen.setProducto(toUpdate);
                imagen.setRuta("/uploads/" + fileName);
                imagenRepository.save(imagen);
            }
        }

        // Crear DTO para la respuesta
        ProductoDTO result = new ProductoDTO();
        result.setId(toUpdate.getId());
        result.setNombre(toUpdate.getNombre());
        result.setDescripcion(toUpdate.getDescripcion());

        // Mapear categoría
        if (toUpdate.getCategoria() != null) {
            ProductoDTO.CategoriaDTO categoriaDTO = new ProductoDTO.CategoriaDTO();
            categoriaDTO.setId(toUpdate.getCategoria().getId());
            categoriaDTO.setTitulo(toUpdate.getCategoria().getTitulo());
            result.setCategoria(categoriaDTO);
        }

        // Mapear imágenes
        List<Imagen> updatedImagenes = imagenRepository.findByProductoId(toUpdate.getId());
        List<ProductoDTO.ImagenDTO> imagenDTOs = updatedImagenes.stream().map(imagen -> {
            ProductoDTO.ImagenDTO imagenDTO = new ProductoDTO.ImagenDTO();
            imagenDTO.setId(imagen.getId());
            imagenDTO.setRuta(imagen.getRuta());
            return imagenDTO;
        }).toList();
        result.setImagenes(imagenDTOs);

        // Mapear características
        if (toUpdate.getFeatures() != null) {
            Set<FeatureDTO> featureDTOs = toUpdate.getFeatures().stream().map(feature -> {
                FeatureDTO featureDTO = new FeatureDTO();
                featureDTO.setId(feature.getId());
                featureDTO.setNombre(feature.getNombre());
                featureDTO.setIcono(feature.getIcono());
                return featureDTO;
            }).collect(Collectors.toSet());
            result.setFeatures(featureDTOs);
        }

        return result;
    }

    /**
     * Crea un nuevo producto usando datos de un DTO.
     * @param productoDTO Datos del producto (nombre, descripción)
     * @param imagenes Imágenes (opcional)
     * @param categoriaId ID de la categoría (opcional)
     * @param featureIds IDs de las características (opcional)
     * @return ProductoDTO creado
     * @throws IOException si falla el manejo de imágenes
     */
    public ProductoDTO create(ProductoDTO productoDTO, List<MultipartFile> imagenes, Long categoriaId, List<Long> featureIds) throws IOException {
        Producto producto = new Producto();
        producto.setNombre(productoDTO.getNombre());
        producto.setDescripcion(productoDTO.getDescripcion());

        // Asignar categoría
        if (categoriaId != null) {
            Optional<Categoria> categoria = categoriaRepository.findById(categoriaId);
            producto.setCategoria(categoria.orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada")));
        }

        // Asignar características
        if (featureIds != null) {
            Set<Feature> features = featureIds.stream()
                    .map(fid -> featureRepository.findById(fid)
                            .orElseThrow(() -> new IllegalArgumentException("Característica no encontrada: " + fid)))
                    .collect(Collectors.toSet());
            producto.setFeatures(features);
        }

        // Guardar producto
        productoRepository.save(producto);

        // Manejar imágenes
        if (imagenes != null && !imagenes.isEmpty()) {
            for (MultipartFile file : imagenes) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get("uploads/" + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, file.getBytes());
                Imagen imagen = new Imagen();
                imagen.setProducto(producto);
                imagen.setRuta("/uploads/" + fileName);
                imagenRepository.save(imagen);
            }
        }

        // Crear DTO para la respuesta
        ProductoDTO result = new ProductoDTO();
        result.setId(producto.getId());
        result.setNombre(producto.getNombre());
        result.setDescripcion(producto.getDescripcion());

        // Mapear categoría
        if (producto.getCategoria() != null) {
            ProductoDTO.CategoriaDTO categoriaDTO = new ProductoDTO.CategoriaDTO();
            categoriaDTO.setId(producto.getCategoria().getId());
            categoriaDTO.setTitulo(producto.getCategoria().getTitulo());
            result.setCategoria(categoriaDTO);
        }

        // Mapear imágenes
        List<Imagen> createdImagenes = imagenRepository.findByProductoId(producto.getId());
        List<ProductoDTO.ImagenDTO> imagenDTOs = createdImagenes.stream().map(imagen -> {
            ProductoDTO.ImagenDTO imagenDTO = new ProductoDTO.ImagenDTO();
            imagenDTO.setId(imagen.getId());
            imagenDTO.setRuta(imagen.getRuta());
            return imagenDTO;
        }).toList();
        result.setImagenes(imagenDTOs);

        // Mapear características
        if (producto.getFeatures() != null) {
            Set<FeatureDTO> featureDTOs = producto.getFeatures().stream().map(feature -> {
                FeatureDTO featureDTO = new FeatureDTO();
                featureDTO.setId(feature.getId());
                featureDTO.setNombre(feature.getNombre());
                featureDTO.setIcono(feature.getIcono());
                return featureDTO;
            }).collect(Collectors.toSet());
            result.setFeatures(featureDTOs);
        }

        return result;
    }

    /**
     * Elimina un producto por su ID.
     * @param id ID del producto
     * @return true si se eliminó, false si no existe
     */
    public boolean delete(Long id) {
        if (!productoRepository.existsById(id)) {
            return false;
        }
        imagenRepository.deleteByProductoId(id);
        productoRepository.deleteById(id);
        return true;
    }

    public List<ProductoDTO> searchProducts(String query, LocalDate startDate, LocalDate endDate) {
        log.info("Buscando productos con query: {}, startDate: {}, endDate: {}", query, startDate, endDate);
        List<Producto> productos;
        if (query == null || query.trim().isEmpty()) {
            productos = productoRepository.findAll();
        } else {
            productos = productoRepository.findByNombreContainingIgnoreCase(query);
        }

        if (startDate != null && endDate != null) {
            productos = productos.stream()
                    .filter(p -> isProductAvailable(p.getId(), startDate, endDate))
                    .collect(Collectors.toList());
        }

        return productos.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<String> getProductSuggestions(String query) {
        log.info("Obteniendo sugerencias para query: {}", query);
        if (query == null || query.trim().isEmpty()) {
            return List.of();
        }
        return productoRepository.findByNombreContainingIgnoreCase(query)
                .stream()
                .map(Producto::getNombre)
                .limit(5)
                .collect(Collectors.toList());
    }

    public List<AvailabilityDTO> getProductAvailability(Long productId) {
        log.info("Obteniendo disponibilidad para producto ID: {}", productId);
        List<Reserva> reservas = reservaRepository.findByProductoIdAndEstado(productId, "DISPONIBLE");
        return reservas.stream()
                .map(reserva -> {
                    AvailabilityDTO dto = new AvailabilityDTO();
                    dto.setId(reserva.getId());
                    dto.setFechaInicio(reserva.getFechaInicio());
                    dto.setFechaFin(reserva.getFechaFin());
                    dto.setEstado(reserva.getEstado());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    private boolean isProductAvailable(Long productId, LocalDate startDate, LocalDate endDate) {
        List<Reserva> reservas = reservaRepository.findByProductoIdAndFechaInicioGreaterThanEqualAndFechaFinLessThanEqual(
                productId, startDate, endDate);
        return reservas.stream().anyMatch(r -> r.getEstado().equals("DISPONIBLE"));
    }

    private ProductoDTO convertToDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());

        if (producto.getCategoria() != null) {
            ProductoDTO.CategoriaDTO categoriaDTO = new ProductoDTO.CategoriaDTO();
            categoriaDTO.setId(producto.getCategoria().getId());
            categoriaDTO.setTitulo(producto.getCategoria().getTitulo());
            dto.setCategoria(categoriaDTO);
        }

        List<Imagen> imagenes = imagenRepository.findByProductoId(producto.getId());
        List<ProductoDTO.ImagenDTO> imagenDTOs = imagenes.stream().map(imagen -> {
            ProductoDTO.ImagenDTO imagenDTO = new ProductoDTO.ImagenDTO();
            imagenDTO.setId(imagen.getId());
            imagenDTO.setRuta(imagen.getRuta());
            return imagenDTO;
        }).toList();
        dto.setImagenes(imagenDTOs);

        if (producto.getFeatures() != null) {
            Set<FeatureDTO> featureDTOs = producto.getFeatures().stream().map(feature -> {
                FeatureDTO featureDTO = new FeatureDTO();
                featureDTO.setId(feature.getId());
                featureDTO.setNombre(feature.getNombre());
                featureDTO.setIcono(feature.getIcono());
                return featureDTO;
            }).collect(Collectors.toSet());
            dto.setFeatures(featureDTOs);
        }

        return dto;
    }
}