package com.projetogs.mylibrary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.projetogs.mylibrary.dto.UserDTO;

@SpringBootTest(properties = {
        "api.security.token.secret=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"
})
@Testcontainers
@ActiveProfiles("test")
public class UserServiceTest {

    @Autowired
    private UserService service;

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

        registry.add("spring.data.mongodb.database", () -> "mylibrarytest");
    }

    @ParameterizedTest
    @CsvSource({
            "João Silva, joao@email.com, senha123, 01001-000, Rua Direita, Centro, São Paulo, SP, 100, Ap 1",
            "Maria Souza, maria@email.com, abc@123, 20040-002, Av Rio Branco, Centro, Rio de Janeiro, RJ, 500, "
    })
    @DisplayName("Deve cadastrar o usuário correto e recusar os inválidos")
    public void testAddNewUser(String name, String email, String password, String zipCode,
            String street, String neighborhood, String city, String state,
            String number, String complement) {

        UserDTO inputDto = new UserDTO(
                name, email, password, zipCode, street,
                neighborhood, city, state, number, complement);
        UserDTO result = service.addNewUser(inputDto);
        
        assertEquals(name, result.name());
        assertEquals(email, result.email());
        assertNotNull(result.password());
        assertEquals(city, result.city());

    }
}