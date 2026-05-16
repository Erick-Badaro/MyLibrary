package com.projetogs.mylibrary.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.projetogs.mylibrary.dto.UserDTO;
import com.projetogs.mylibrary.entities.User;
import com.projetogs.mylibrary.repository.UserRepository;

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
        user.setZipCode(dto.zipCode());
        user.setStreet(dto.street());
        user.setNeighborhood(dto.neighborhood());
        user.setCity(dto.city());
        user.setState(dto.state());
        user.setNumber(dto.number());
        user.setComplement(dto.complement());
        
        User saved = repository.save(user);
        
        return new UserDTO(
            saved.getName(), 
            saved.getEmail(), 
            saved.getPassword(),
            saved.getZipCode(),
            saved.getStreet(),
            saved.getNeighborhood(),
            saved.getCity(),
            saved.getState(),
            saved.getNumber(),
            saved.getComplement()
        );
    }
}
