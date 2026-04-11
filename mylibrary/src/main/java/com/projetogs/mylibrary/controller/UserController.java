package com.projetogs.mylibrary.controller;

import com.projetogs.mylibrary.dto.UserDTO;
import com.projetogs.mylibrary.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService service;

    @PostMapping("/singup")
    public ResponseEntity<UserDTO> addNew(@RequestBody @Valid UserDTO dto) {
        UserDTO saved = service.addNewUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }
}
