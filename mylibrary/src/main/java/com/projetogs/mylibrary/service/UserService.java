package com.projetogs.mylibrary.service;

import com.projetogs.mylibrary.dto.UserDTO;
import com.projetogs.mylibrary.entities.User;
import com.projetogs.mylibrary.repository.BookRepository;
import com.projetogs.mylibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository repository;

    public UserDTO addNewUser(UserDTO dto) {
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        User saved = repository.save(user);
        return new UserDTO(saved.getName(), saved.getEmail(), saved.getPassword());
    }
}
