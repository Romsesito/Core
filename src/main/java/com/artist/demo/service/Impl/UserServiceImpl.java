package com.artist.demo.service.Impl;

import com.artist.demo.dto.UserDTO;
import com.artist.demo.dto.UserRegistrationDTO;
import com.artist.demo.dto.LoginRequestDTO;
import com.artist.demo.entity.User;
import com.artist.demo.enums.Role;
import com.artist.demo.exception.EmailAlreadyExistsException;
import com.artist.demo.exception.InvalidCredentialsException;
import com.artist.demo.exception.ResourceNotFoundException;
import com.artist.demo.exception.CustomUsernameNotFoundException;
import com.artist.demo.exception.UsernameAlreadyExistsException;
import com.artist.demo.repository.UserRepository;
import com.artist.demo.service.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDTO registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.existsByUsername(registrationDTO.getUsername())) {
            throw new UsernameAlreadyExistsException("El nombre de usuario '" + registrationDTO.getUsername() + "' ya existe.");
        }
        if (userRepository.existsByEmail(registrationDTO.getEmail())) {
            throw new EmailAlreadyExistsException("El email '" + registrationDTO.getEmail() + "' ya está registrado.");
        }

        User user = new User();
        user.setUsername(registrationDTO.getUsername());
        user.setEmail(registrationDTO.getEmail());

        user.setPassword(registrationDTO.getPassword());
        user.setRole(registrationDTO.getRole());
        user.setEnabled(true);

        User savedUser = userRepository.save(user);

        return modelMapper.map(savedUser, UserDTO.class);
    }

    @Override
    public UserDTO loginUser(LoginRequestDTO loginRequestDTO) {
        User user = userRepository.findByUsername(loginRequestDTO.getUsername())
                .orElseThrow(() -> new CustomUsernameNotFoundException("Usuario o contraseña inválidos."));


        if (loginRequestDTO.getPassword().equals(user.getPassword())) {
            return modelMapper.map(user, UserDTO.class);
        } else {
            throw new InvalidCredentialsException("Usuario o contraseña inválidos.");
        }
    }

    @Override
    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id.toString())); // Asegúrate que fieldValue sea String
        return modelMapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomUsernameNotFoundException("Usuario no encontrado con el nombre de usuario: " + username));
        return modelMapper.map(user, UserDTO.class);
    }


    @Override
    public List<UserDTO> findUsersByRole(Role role) {
        List<User> users = userRepository.findAllByRole(role);
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }




    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .collect(Collectors.toList());
    }
}