package com.example.desafio_profesional_back.controllers;

import com.example.desafio_profesional_back.dto.UserDTO;
import com.example.desafio_profesional_back.models.User;
import com.example.desafio_profesional_back.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        try {
            UserDTO updated = userService.updateRole(id, userDTO.getRole());
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el rol");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
            if (user != null) {
                return ResponseEntity.ok(new UserDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getNombre(),
                        user.getApellido(),
                        user.getRole()
                ));
            }
            return ResponseEntity.status(401).body("Credenciales inválidas");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al autenticar");
        }
    }
}

class LoginRequest {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}