package com.example.desafio_profesional_back.repositories;

import com.example.desafio_profesional_back.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
