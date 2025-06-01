package com.artist.demo.service;

import com.artist.demo.dto.UserDTO;
import com.artist.demo.dto.UserRegistrationDTO;
import com.artist.demo.dto.LoginRequestDTO;
import com.artist.demo.enums.Role;

import java.util.List;

public interface UserService {
    UserDTO registerUser(UserRegistrationDTO registrationDTO);
    UserDTO loginUser(LoginRequestDTO loginRequestDTO);
    UserDTO findUserById(Long id);
    UserDTO findUserByUsername(String username);
    List<UserDTO> findUsersByRole(Role role);

    List<UserDTO> getAllUsers();
}