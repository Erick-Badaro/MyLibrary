package com.projetogs.mylibrary.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projetogs.mylibrary.dto.UserDTO;
import com.projetogs.mylibrary.dto.UserLoginDTO;
import com.projetogs.mylibrary.repository.UserRepository;
import com.projetogs.mylibrary.service.UserService;

@SpringBootTest(properties = {
        "api.security.token.secret=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"
})
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class UserControllerE2ETest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    private UserService service;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

        registry.add("spring.data.mongodb.database", () -> "mylibrarytest");
    }

    @BeforeEach
    void cleanUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("E2E api user - POST /user/singup/ - deve cadastrar o usuário")
    public void shouldCreateUser() throws Exception {
        UserDTO user = new UserDTO(
                "João Silva",
                "joao@email.com",
                "senha123@Valida",
                "01001-000",
                "Rua Direita",
                "Centro",
                "São Paulo",
                "SP",
                "100",
                "Ap 1");

        String json = objectMapper.writeValueAsString(user);

        mvc.perform(post("/user/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("E2E api user - POST /user/login - deve logar e retornar o token")
    public void shouldLoginUser() throws Exception {
        UserDTO user = new UserDTO(
                "João Silva",
                "joao@email.com",
                "senha123@Valida",
                "01001-000",
                "Rua Direita",
                "Centro",
                "São Paulo",
                "SP",
                "100",
                "Ap 1");
        service.addNewUser(user);

        UserLoginDTO login = new UserLoginDTO("joao@email.com",
                "senha123@Valida");

        String json = objectMapper.writeValueAsString(login);

        mvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.name").value("João Silva"));
    }

    @Test
    @DisplayName("E2E api user - POST /user/login - deve retornar 401 para senha incorreta mas válida no formato")
    public void shouldLoginUserUnauthorized() throws Exception {
        UserDTO user = new UserDTO(
                "João Silva",
                "joao@email.com",
                "Senha@123",
                "01001-000",
                "Rua Direita",
                "Centro",
                "São Paulo",
                "SP",
                "100",
                "Ap 1");
        service.addNewUser(user);

        
        UserLoginDTO login = new UserLoginDTO("joao@email.com", "Errada@123");

        String json = objectMapper.writeValueAsString(login);

        mvc.perform(post("/user/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("E2E api user - GET /user/zipcode/{zipCode} - Deve retornar o ZipCodeResponse e http 200(ok)")
    public void chouldReturnZipcode() throws Exception {
        String zipCode = "01001-000";
        mvc.perform(get("/user/zipcode/" + zipCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cep").exists());
    }
}
