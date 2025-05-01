package com.example.desafio_profesional_back.services;

import com.example.desafio_profesional_back.dto.PuntuacionDTO;
import com.example.desafio_profesional_back.models.Puntuacion;
import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.models.Reserva;
import com.example.desafio_profesional_back.models.User;
import com.example.desafio_profesional_back.repositories.PuntuacionRepository;
import com.example.desafio_profesional_back.repositories.ReservaRepository;
import com.example.desafio_profesional_back.repositories.UserRepository;
import com.example.desafio_profesional_back.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        User user = userRepository.findById(puntuacionDTO.getUsuarioId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Producto producto = productoRepository.findById(puntuacionDTO.getProductoId())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        if (puntuacionRepository.existsByProductoIdAndUsuarioId(puntuacionDTO.getProductoId(), puntuacionDTO.getUsuarioId())) {
            throw new RuntimeException("El usuario ya puntuó este producto");
        }

        if (!hasReservation(puntuacionDTO.getUsuarioId(), puntuacionDTO.getProductoId())) {
            throw new RuntimeException("El usuario no tiene una reserva finalizada para este producto");
        }

        if (puntuacionDTO.getEstrellas() < 1 || puntuacionDTO.getEstrellas() > 5) {
            throw new RuntimeException("La puntuación debe estar entre 1 y 5 estrellas");
        }

        Puntuacion puntuacion = new Puntuacion();
        puntuacion.setUsuario(user);
        puntuacion.setProducto(producto);
        puntuacion.setEstrellas(puntuacionDTO.getEstrellas());
        puntuacion = puntuacionRepository.save(puntuacion);

        puntuacionDTO.setId(puntuacion.getId());
        return puntuacionDTO;
    }

    public boolean hasReservation(Long usuarioId, Long productoId) {
        List<Reserva> reservas = reservaRepository.findByProductoIdAndUserId(productoId, usuarioId);
        return reservas.stream().anyMatch(r -> r.getEstado().equals("FINALIZADA"));
    }
}