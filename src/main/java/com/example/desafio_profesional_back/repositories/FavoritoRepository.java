package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.Favorito;
import com.example.desafio_profesional_back.models.Producto;
import com.example.desafio_profesional_back.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoritoRepository extends JpaRepository<Favorito, Long> {
    List<Favorito> findByUsuario(User usuario);
    boolean existsByUsuarioAndProducto(User usuario, Producto producto);
}