package com.example.desafio_profesional_back.controllers;

import com.example.desafio_profesional_back.dto.FavoritoDTO;
import com.example.desafio_profesional_back.dto.ProductoDTO;
import com.example.desafio_profesional_back.services.FavoritoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/favoritos")
public class FavoritoController {
    @Autowired
    private FavoritoService favoritoService;

    @PostMapping
    public ResponseEntity<FavoritoDTO> addFavorito(@RequestBody FavoritoDTO favoritoDTO) {
        return ResponseEntity.ok(favoritoService.addFavorito(favoritoDTO));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFavorito(@PathVariable Long productId, @RequestBody Map<String, Long> body) {
        favoritoService.removeFavorito(body.get("userId"), productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getFavoritos(@RequestParam Long userId) {
        return ResponseEntity.ok(favoritoService.getFavoritos(userId));
    }
}