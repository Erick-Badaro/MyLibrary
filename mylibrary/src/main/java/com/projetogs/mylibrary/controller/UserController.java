package com.projetogs.mylibrary.controller;

import com.projetogs.mylibrary.dto.UserDTO;
import com.projetogs.mylibrary.dto.UserLoginDTO;
import com.projetogs.mylibrary.jwt.JwtService;
import com.projetogs.mylibrary.security.UserSystem;
import com.projetogs.mylibrary.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private AuthenticationManager authManager;

    @Autowired
    private JwtService  jwtService;

    @Autowired
    private UserService service;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> addNew(@RequestBody @Valid UserDTO dto) {
        UserDTO saved = service.addNewUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PostMapping("/login")
    public ResponseEntity<responseLogin> login(@RequestBody @Valid UserLoginDTO dto) {
        try {
            Authentication auth = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            dto.email(),
                            dto.password()));
            UserSystem user = (UserSystem) auth.getPrincipal();
            String token = jwtService.newToken(user);
            return ResponseEntity.ok().body(new responseLogin(user.getName(), token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    public record responseLogin(String name, String token) { }
}
