package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureRepository extends JpaRepository<Feature, Long> {
}