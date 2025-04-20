package com.example.desafio_profesional_back.services;

import com.example.desafio_profesional_back.dto.FeatureDTO;
import com.example.desafio_profesional_back.models.Feature;
import com.example.desafio_profesional_back.repositories.FeatureRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FeatureService {

    @Autowired
    private FeatureRepository featureRepository;

    public List<FeatureDTO> findAll() {
        log.info("Obteniendo todas las características");
        return featureRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public FeatureDTO create(FeatureDTO dto) {
        log.info("Creando nueva característica: {}", dto.getNombre());
        Feature feature = new Feature();
        feature.setNombre(dto.getNombre());
        feature.setIcono(dto.getIcono());
        Feature saved = featureRepository.save(feature);
        return convertToDTO(saved);
    }

    public FeatureDTO update(Long id, FeatureDTO dto) {
        log.info("Actualizando característica ID: {}", id);
        Optional<Feature> optional = featureRepository.findById(id);
        if (!optional.isPresent()) {
            log.warn("Característica no encontrada: {}", id);
            throw new IllegalArgumentException("Característica no encontrada");
        }
        Feature feature = optional.get();
        feature.setNombre(dto.getNombre());
        feature.setIcono(dto.getIcono());
        Feature updated = featureRepository.save(feature);
        return convertToDTO(updated);
    }

    public void delete(Long id) {
        log.info("Eliminando característica ID: {}", id);
        if (!featureRepository.existsById(id)) {
            log.warn("Característica no encontrada: {}", id);
            throw new IllegalArgumentException("Característica no encontrada");
        }
        featureRepository.deleteById(id);
    }

    private FeatureDTO convertToDTO(Feature feature) {
        FeatureDTO dto = new FeatureDTO();
        dto.setId(feature.getId());
        dto.setNombre(feature.getNombre());
        dto.setIcono(feature.getIcono());
        return dto;
    }
}