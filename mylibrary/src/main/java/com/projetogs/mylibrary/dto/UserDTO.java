package com.projetogs.mylibrary.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserDTO(
        @NotBlank
        String name,

        @Email
        @NotBlank
        String email,

        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z0-9@$!%*?&]{8,}$")
        @Size(min = 8)
        @NotBlank
        String password,
        String zipCode,
        String street,
        String neighborhood,
        String city,
        String state,
        String number,
        String complement
) {
}
