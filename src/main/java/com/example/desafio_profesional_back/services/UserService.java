package com.example.desafio_profesional_back.services;

import com.example.desafio_profesional_back.dto.UserDTO;
import com.example.desafio_profesional_back.models.User;
import com.example.desafio_profesional_back.repositories.UserRepository;
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
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> findAll() {
        log.info("Obteniendo todos los usuarios");
        List<User> users = userRepository.findAll();
        return users.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    public UserDTO updateRole(Long id, String role) {
        log.info("Actualizando rol del usuario con ID: {} a {}", id, role);
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            log.warn("Usuario no encontrado: {}", id);
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        User user = userOpt.get();
        user.setRole(role);
        User updated = userRepository.save(user);
        log.info("Usuario actualizado: {}", updated);
        return convertToDTO(updated);
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setNombre(user.getNombre());
        dto.setApellido(user.getApellido());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        return dto;
    }
}