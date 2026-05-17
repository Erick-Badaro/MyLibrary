package com.projetogs.mylibrary.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.projetogs.mylibrary.dto.UserDTO;
import com.projetogs.mylibrary.repository.UserRepository;

@SpringBootTest(properties = {
        "api.security.token.secret=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"
})
@Testcontainers
@ActiveProfiles("test")
public class UseSystemServiceTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserSystemService service;

    @Autowired
    private UserService userService;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

        registry.add("spring.data.mongodb.database", () -> "mylibrarytest");
    }

    @BeforeEach
    public void setUp() {
        UserDTO inputDto = new UserDTO(
                "Teste Sucesso", "sucesso@email.com", "senha123", "01001-000",
                "Rua A", "Centro", "São Paulo", "SP", "10", "");
        userService.addNewUser(inputDto);
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Deve validar a carga do usuário por e-mail com base na expectativa")
    public void loadUserByUsernameTest() {
        String email = "sucesso@email.com";
        UserDetails userDetails = service.loadUserByUsername(email);
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
    }

    @Test
    @DisplayName("Deve lançar UsernameNotFoundException quando o e-mail não existir")
    public void loadUserByUsernameNotFoundTest() {
        String emailInexistente = "naoexiste@email.com";
        
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            service.loadUserByUsername(emailInexistente);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }
}
