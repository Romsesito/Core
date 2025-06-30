package com.artist.demo.factory;

import com.artist.demo.dto.UserRegistrationDTO;
import com.artist.demo.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserFactory {
    public User createUser(UserRegistrationDTO registrationDTO) {
        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setEmail(registrationDTO.getEmail());
        user.setPassword(registrationDTO.getPassword()); // Considera usar un codificador de contraseñas
        user.setRole(registrationDTO.getRole());
        user.setEnabled(true);
        // Lógica adicional de creación podría ir aquí
        return user;
    }
}
