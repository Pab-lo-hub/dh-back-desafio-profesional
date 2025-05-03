package com.example.desafio_profesional_back.services;

import com.example.desafio_profesional_back.dto.AvailabilityDTO;
import com.example.desafio_profesional_back.dto.FeatureDTO;
import com.example.desafio_profesional_back.dto.PoliticaDTO;
import com.example.desafio_profesional_back.dto.PuntuacionDTO;
import com.example.desafio_profesional_back.dto.ProductoDTO;
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

    @Autowired
    private PoliticaRepository politicaRepository;

    @Autowired
    private PuntuacionRepository puntuacionRepository;

    // Ruta base para almacenar imágenes en el directorio estático
    private static final String UPLOAD_DIR = "src/main/resources/static/uploads/";

    /**
     * Obtiene todos los productos como DTOs.
     * @return Lista de ProductoDTO
     */
    public List<ProductoDTO> findAll() {
        log.info("Obteniendo todos los productos");
        List<Producto> productos = productoRepository.findAll();
        return productos.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    /**
     * Obtiene productos por IDs de categorías.
     * @param categoriaIds Lista de IDs de categorías
     * @return Lista de ProductoDTO
     */
    public List<ProductoDTO> findByCategoriaIds(List<Long> categoriaIds) {
        log.info("Obteniendo productos por categorías: {}", categoriaIds);
        List<Producto> productos = productoRepository.findByCategoriaIdIn(categoriaIds);
        return productos.stream().map(this::convertToDTO).collect(Collectors.toList());
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
        return convertToDTO(productoOpt.get());
    }

    /**
     * Actualiza un producto existente usando datos de un DTO.
     * @param id ID del producto
     * @param productoDTO Datos actualizados (nombre, descripción, precio)
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
        toUpdate.setPrecio(productoDTO.getPrecio());

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
            // Guardar nuevas imágenes en src/main/resources/static/uploads/
            for (MultipartFile file : imagenes) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, file.getBytes());
                Imagen imagen = new Imagen();
                imagen.setProducto(toUpdate);
                imagen.setRuta("/uploads/" + fileName);
                imagenRepository.save(imagen);
            }
        }

        return convertToDTO(toUpdate);
    }

    /**
     * Crea un nuevo producto usando datos de un DTO.
     * @param productoDTO Datos del producto (nombre, descripción, precio)
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
        producto.setPrecio(productoDTO.getPrecio());

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
                Path filePath = Paths.get(UPLOAD_DIR + fileName);
                Files.createDirectories(filePath.getParent());
                Files.write(filePath, file.getBytes());
                Imagen imagen = new Imagen();
                imagen.setProducto(producto);
                imagen.setRuta("/uploads/" + fileName);
                imagenRepository.save(imagen);
            }
        }

        return convertToDTO(producto);
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

    /**
     * Busca productos por nombre y disponibilidad.
     * @param query Nombre del producto (opcional)
     * @param startDate Fecha de inicio (opcional)
     * @param endDate Fecha de fin (opcional)
     * @return Lista de ProductoDTO
     */
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

    /**
     * Obtiene sugerencias de productos basadas en el nombre.
     * @param query Nombre parcial del producto
     * @return Lista de nombres de productos
     */
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

    /**
     * Obtiene la disponibilidad de un producto.
     * @param productId ID del producto
     * @return Lista de AvailabilityDTO con fechas disponibles
     */
    public List<AvailabilityDTO> getProductAvailability(Long productId) {
        log.info("Obteniendo disponibilidad para producto ID: {}", productId);
        List<Reserva> reservas = reservaRepository.findByProductoIdAndEstado(productId, "CONFIRMADA");
        List<AvailabilityDTO> availability = new ArrayList<>();

        LocalDate today = LocalDate.now();
        LocalDate end = today.plusYears(1);
        LocalDate current = today;

        while (!current.isAfter(end)) {
            boolean isAvailable = true;
            for (Reserva reserva : reservas) {
                if (!reserva.getFechaFin().isBefore(current) && !reserva.getFechaInicio().isAfter(current)) {
                    isAvailable = false;
                    break;
                }
            }
            if (isAvailable) {
                LocalDate rangeStart = current;
                LocalDate rangeEnd = current;
                while (rangeEnd.isBefore(end)) {
                    LocalDate nextDay = rangeEnd.plusDays(1);
                    boolean nextDayAvailable = true;
                    for (Reserva reserva : reservas) {
                        if (!reserva.getFechaFin().isBefore(nextDay) && !reserva.getFechaInicio().isAfter(nextDay)) {
                            nextDayAvailable = false;
                            break;
                        }
                    }
                    if (!nextDayAvailable) {
                        break;
                    }
                    rangeEnd = nextDay;
                }
                AvailabilityDTO dto = new AvailabilityDTO();
                dto.setId(0L);
                dto.setFechaInicio(rangeStart);
                dto.setFechaFin(rangeEnd);
                dto.setEstado("DISPONIBLE");
                availability.add(dto);
                current = rangeEnd.plusDays(1);
            } else {
                current = current.plusDays(1);
            }
        }
        return availability;
    }

    /**
     * Verifica si un producto está disponible en un rango de fechas.
     * @param productId ID del producto
     * @param startDate Fecha de inicio
     * @param endDate Fecha de fin
     * @return true si está disponible, false en caso contrario
     */
    private boolean isProductAvailable(Long productId, LocalDate startDate, LocalDate endDate) {
        List<Reserva> reservas = reservaRepository.findByProductoIdAndFechaInicioGreaterThanEqualAndFechaFinLessThanEqual(
                productId, startDate, endDate);
        return reservas.isEmpty();
    }

    /**
     * Convierte un Producto a ProductoDTO.
     * @param producto Entidad Producto
     * @return ProductoDTO
     */
    private ProductoDTO convertToDTO(Producto producto) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setDescripcion(producto.getDescripcion());
        dto.setPrecio(producto.getPrecio());

        // Mapear categoría
        if (producto.getCategoria() != null) {
            ProductoDTO.CategoriaDTO categoriaDTO = new ProductoDTO.CategoriaDTO();
            categoriaDTO.setId(producto.getCategoria().getId());
            categoriaDTO.setTitulo(producto.getCategoria().getTitulo());
            dto.setCategoria(categoriaDTO);
            dto.setCategoria_id(producto.getCategoria().getId());
        }

        // Mapear imágenes
        List<Imagen> imagenes = imagenRepository.findByProductoId(producto.getId());
        List<ProductoDTO.ImagenDTO> imagenDTOs = imagenes.stream().map(imagen -> {
            ProductoDTO.ImagenDTO imagenDTO = new ProductoDTO.ImagenDTO();
            imagenDTO.setId(imagen.getId());
            imagenDTO.setRuta(imagen.getRuta());
            return imagenDTO;
        }).collect(Collectors.toList());
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
     * Obtiene las políticas de un producto por su ID.
     * @param productoId ID del producto
     * @return Lista de PoliticaDTO
     */
    public List<PoliticaDTO> getPoliticasByProductoId(Long productoId) {
        List<Politica> politicas = politicaRepository.findByProductoId(productoId);
        return politicas.stream().map(politica -> {
            PoliticaDTO dto = new PoliticaDTO();
            dto.setId(politica.getId());
            dto.setTitulo(politica.getTitulo());
            dto.setDescripcion(politica.getDescripcion());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Obtiene las puntuaciones de un producto por su ID.
     * @param productoId ID del producto
     * @return Lista de PuntuacionDTO
     */
    public List<PuntuacionDTO> getPuntuacionesByProductoId(Long productoId) {
        List<Puntuacion> puntuaciones = puntuacionRepository.findByProductoId(productoId);
        return puntuaciones.stream().map(puntuacion -> {
            PuntuacionDTO dto = new PuntuacionDTO();
            dto.setId(puntuacion.getId());
            dto.setProductoId(puntuacion.getProducto().getId());
            dto.setUsuarioId(puntuacion.getUser().getId());
            dto.setEstrellas(puntuacion.getEstrellas());
            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Verifica si un usuario puede puntuar un producto (si tiene una reserva finalizada).
     * @param productoId ID del producto
     * @param usuarioId ID del usuario
     * @return true si puede puntuar, false en caso contrario
     */
    public boolean canUserRateProducto(Long productoId, Long usuarioId) {
        List<Reserva> reservas = reservaRepository.findByProductoIdAndUsuarioId(productoId, usuarioId);
        return reservas.stream().anyMatch(r -> r.getEstado().equals("FINALIZADA")) &&
                !puntuacionRepository.existsByProducto_IdAndUser_Id(productoId, usuarioId);
    }
}