package com.noda.api.controllers;

import com.noda.api.dtos.UserRequestDTO;
import com.noda.api.dtos.UserResponseDTO;
import com.noda.api.models.User;
import com.noda.api.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO response = userService.save(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        UserResponseDTO response = userService.findUserByIdMapped(id);
        return ResponseEntity.ok(response);
    }
}



