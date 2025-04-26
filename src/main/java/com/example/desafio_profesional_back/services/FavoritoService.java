package com.example.desafio_profesional_back.services;

import com.example.desafio_profesional_back.dto.FavoritoDTO;
import com.example.desafio_profesional_back.dto.FeatureDTO;
import com.example.desafio_profesional_back.dto.ProductoDTO;
import com.example.desafio_profesional_back.models.Favorito;
import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.models.User;
import com.example.desafio_profesional_back.repositories.FavoritoRepository;
import com.example.desafio_profesional_back.repositories.ProductoRepository;
import com.example.desafio_profesional_back.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoritoService {
    @Autowired
    private FavoritoRepository favoritoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductoRepository productoRepository;

    public FavoritoDTO addFavorito(FavoritoDTO favoritoDTO) {
        User user = userRepository.findById(favoritoDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(favoritoDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (favoritoRepository.existsByUsuarioAndProducto(user, producto)) {
            throw new RuntimeException("El producto ya está en favoritos");
        }

        Favorito favorito = new Favorito();
        favorito.setUsuario(user);
        favorito.setProducto(producto);
        favorito = favoritoRepository.save(favorito);

        favoritoDTO.setId(favorito.getId());
        return favoritoDTO;
    }

    public void removeFavorito(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        Favorito favorito = favoritoRepository.findByUsuario(user).stream()
                .filter(f -> f.getProducto().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Favorito no encontrado"));

        favoritoRepository.delete(favorito);
    }

    public List<ProductoDTO> getFavoritos(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return favoritoRepository.findByUsuario(user).stream()
                .map(favorito -> {
                    Producto producto = favorito.getProducto();
                    ProductoDTO dto = new ProductoDTO();
                    dto.setId(producto.getId());
                    dto.setNombre(producto.getNombre());
                    dto.setDescripcion(producto.getDescripcion());
                    dto.setPrecio(producto.getPrecio());
                    if (producto.getCategoria() != null) {
                        ProductoDTO.CategoriaDTO categoriaDTO = new ProductoDTO.CategoriaDTO();
                        categoriaDTO.setId(producto.getCategoria().getId());
                        categoriaDTO.setTitulo(producto.getCategoria().getTitulo());
                        dto.setCategoria(categoriaDTO);
                    }
                    dto.setImagenes(producto.getImagenes().stream()
                            .map(img -> {
                                ProductoDTO.ImagenDTO imgDto = new ProductoDTO.ImagenDTO();
                                imgDto.setId(img.getId());
                                imgDto.setRuta(img.getRuta());
                                return imgDto;
                            })
                            .collect(Collectors.toList()));
                    dto.setFeatures(producto.getFeatures().stream()
                            .map(feat -> {
                                FeatureDTO featDto = new FeatureDTO();
                                featDto.setId(feat.getId());
                                featDto.setNombre(feat.getNombre());
                                featDto.setIcono(feat.getIcono());
                                return featDto;
                            })
                            .collect(Collectors.toSet()));
                    dto.setPoliticas(producto.getPoliticas());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}