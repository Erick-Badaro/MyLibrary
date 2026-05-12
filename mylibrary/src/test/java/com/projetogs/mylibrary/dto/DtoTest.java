package com.projetogs.mylibrary.dto;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.context.ActiveProfiles;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

@ActiveProfiles("test")
public class DtoTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @ParameterizedTest
    @CsvSource({
            "'João Silva', 'joao@email.com', 'Senha@123', true",

            "'', 'joao@email.com', 'Senha@123', false",

            "'João Silva', 'email-sem-arroba.com', 'Senha@123', false",
            "'João Silva', '', 'Senha@123', false",

            "'João Silva', 'joao@email.com', 'senha123', false",
            "'João Silva', 'joao@email.com', 'SENHA123!', false",
            "'João Silva', 'joao@email.com', 'Senha!', false",
            "'João Silva', 'joao@email.com', 'Senha123', false",
            "'João Silva', 'joao@email.com', ' ', false"
    })
    public void testUserDTO(String name, String email, String password, boolean isValid) {
        UserDTO dto = new UserDTO(name, email, password, "01001-000", "Rua A", "Centro", "SP", "SP", "1", "");

        var violations = validator.validate(dto);

        if (isValid) {
            assertTrue(violations.isEmpty());
        } else {
            assertFalse(violations.isEmpty());
        }
    }

    @ParameterizedTest
        if (isValid) {
            assertTrue(violations.isEmpty());
        } else {
            assertFalse(violations.isEmpty());
        }
    public void testUserLoginDTO(String email, String password, boolean isValid) {
        UserLoginDTO dto = new UserLoginDTO(email, password);

        var violations = validator.validate(dto);

        if (isValid) {
            assertTrue(violations.isEmpty());
        } else {
            assertFalse(violations.isEmpty());
        }
    }

}
