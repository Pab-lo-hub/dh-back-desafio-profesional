package com.example.desafio_profesional_back.services;

import com.example.desafio_profesional_back.dto.PuntuacionDTO;
import com.example.desafio_profesional_back.models.Puntuacion;
import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.models.User;
import com.example.desafio_profesional_back.repositories.PuntuacionRepository;
import com.example.desafio_profesional_back.repositories.ReservaRepository;
import com.example.desafio_profesional_back.repositories.UserRepository;
import com.example.desafio_profesional_back.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PuntuacionService {
    @Autowired
    private PuntuacionRepository puntuacionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    public PuntuacionDTO addPuntuacion(PuntuacionDTO puntuacionDTO) {
        User user = userRepository.findById(puntuacionDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(puntuacionDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (puntuacionRepository.existsByUsuarioAndProducto(user, producto)) {
            throw new RuntimeException("El usuario ya puntuó este producto");
        }

        if (!hasReservation(puntuacionDTO.getUserId(), puntuacionDTO.getProductId())) {
            throw new RuntimeException("El usuario no tiene una reserva para este producto");
        }

        Puntuacion puntuacion = new Puntuacion();
        puntuacion.setUsuario(user);
        puntuacion.setProducto(producto);
        puntuacion.setRating(puntuacionDTO.getRating());
        puntuacion = puntuacionRepository.save(puntuacion);

        puntuacionDTO.setId(puntuacion.getId());
        return puntuacionDTO;
    }

    public boolean hasReservation(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        return reservaRepository.existsByUserAndProducto(user, producto);
    }
}