package com.projetogs.mylibrary.dto;

import jakarta.validation.constraints.*;

public record UserLoginDTO(
        @Email
        @NotBlank
        String email,

        @Size(min = 8)
        @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[@$!%*?&])[A-Za-z0-9@$!%*?&]{8,}$")
        String password
) {
}
