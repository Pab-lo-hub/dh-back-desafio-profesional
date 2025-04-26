package com.example.desafio_profesional_back.controllers;

import com.example.desafio_profesional_back.dto.PuntuacionDTO;
import com.example.desafio_profesional_back.services.PuntuacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/puntuaciones")
public class PuntuacionController {
    @Autowired
    private PuntuacionService puntuacionService;

    @PostMapping
    public ResponseEntity<PuntuacionDTO> addPuntuacion(@RequestBody PuntuacionDTO puntuacionDTO) {
        return ResponseEntity.ok(puntuacionService.addPuntuacion(puntuacionDTO));
    }

    @GetMapping
    public ResponseEntity<Map<String, Boolean>> checkReservation(
            @RequestParam Long userId, @RequestParam Long productId) {
        boolean hasReservation = puntuacionService.hasReservation(userId, productId);
        return ResponseEntity.ok(Map.of("hasReservation", hasReservation));
    }
}