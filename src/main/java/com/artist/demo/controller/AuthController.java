package com.artist.demo.controller;

import com.artist.demo.dto.LoginRequestDTO;
import com.artist.demo.dto.UserDTO;
import com.artist.demo.dto.UserRegistrationDTO;
import com.artist.demo.exception.CustomUsernameNotFoundException;
import com.artist.demo.exception.EmailAlreadyExistsException;
import com.artist.demo.exception.InvalidCredentialsException;
import com.artist.demo.exception.UsernameAlreadyExistsException;
import com.artist.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;


    @Autowired
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            UserDTO registeredUser = userService.registerUser(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
        } catch (UsernameAlreadyExistsException | EmailAlreadyExistsException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error durante el registro.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        try {
            UserDTO userDTO = userService.loginUser(loginRequestDTO);
            return ResponseEntity.ok(userDTO);
        } catch (CustomUsernameNotFoundException | InvalidCredentialsException e) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        } catch (Exception e) {

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error durante el inicio de sesión.");
        }
    }
}