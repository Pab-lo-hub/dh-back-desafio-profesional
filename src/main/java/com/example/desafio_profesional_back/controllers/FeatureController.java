package com.example.desafio_profesional_back.controllers;

import com.example.desafio_profesional_back.dto.FeatureDTO;
import com.example.desafio_profesional_back.services.FeatureService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/features")
@RequiredArgsConstructor
public class FeatureController {

    @Autowired
    private FeatureService featureService;

    @GetMapping

    public ResponseEntity<List<FeatureDTO>> getAllFeatures() {
        List<FeatureDTO> features = featureService.findAll();
        return ResponseEntity.ok(features);
    }

    @PostMapping
    public ResponseEntity<FeatureDTO> createFeature(@RequestBody FeatureDTO featureDTO) {
        FeatureDTO created = featureService.create(featureDTO);
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateFeature(@PathVariable Long id, @RequestBody FeatureDTO featureDTO) {
        try {
            FeatureDTO updated = featureService.update(id, featureDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFeature(@PathVariable Long id) {
        try {
            featureService.delete(id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        }
    }
}