package com.projetogs.mylibrary.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
import com.projetogs.mylibrary.dto.BookDTO;
import com.projetogs.mylibrary.dto.BookDTOGet;
import com.projetogs.mylibrary.dto.UserDTO;
import com.projetogs.mylibrary.entities.User;
import com.projetogs.mylibrary.enums.ReadingStatus;
import com.projetogs.mylibrary.repository.UserRepository;
import com.projetogs.mylibrary.security.UserSystem;
import com.projetogs.mylibrary.service.BookService;
import com.projetogs.mylibrary.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest(properties = {
                "api.security.token.secret=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"
})
@Testcontainers
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class BookControllerE2ETest {

        @Container
        static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

        @Autowired
        private MockMvc mvc;

        @Autowired
        private UserService service;

        @Autowired
        private UserRepository repository;

        @Autowired
        private BookService bookService;

        @Autowired
        private ObjectMapper objectMapper;

        private UserSystem userSystem;
        private String userIdTest;
        private User user;

        @DynamicPropertySource
        static void setProperties(DynamicPropertyRegistry registry) {
                registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

                registry.add("spring.data.mongodb.database", () -> "mylibrarytest");
        }

        @BeforeEach
        void setUp() {
                UserDTO inputDto = new UserDTO(
                                "Teste Sucesso", "sucesso@email.com", "senha123", "01001-000",
                                "Rua A", "Centro", "São Paulo", "SP", "10", "");

                BookDTO livroParaLer = new BookDTO(
                                "O Silmarillion",
                                "J.R.R. Tolkien",
                                "HarperCollins",
                                "Fantasia",
                                ReadingStatus.WANNA_READ);

                BookDTO livroLendo = new BookDTO(
                                "1984",
                                "George Orwell",
                                "Companhia das Letras",
                                "Distopia",
                                ReadingStatus.READING);

                BookDTO livroLido = new BookDTO(
                                "O Hobbit",
                                "J.R.R. Tolkien",
                                "HarperCollins",
                                "Fantasia",
                                ReadingStatus.READ);

                service.addNewUser(inputDto);
                User entity = repository.findByEmail("sucesso@email.com")
                                .orElseThrow(() -> new RuntimeException("Usuário não encontrado no banco real"));
                this.userSystem = new UserSystem(entity.getId(), entity.getName(), entity.getEmail());

                userIdTest = entity.getId();

                bookService.createBook(userIdTest, livroParaLer);
                bookService.createBook(userIdTest, livroLendo);
                bookService.createBook(userIdTest, livroLido);
        }

        @AfterEach
        void tearDown() {
                repository.deleteAll();
        }

        @Test
        @DisplayName("E2E api book - GET /books - deve retornar 200")
        public void shouldReturnBookList() throws Exception {
                mvc.perform(get("/books")
                                .with(user(userSystem)))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(3))
                                .andExpect(jsonPath("$[0].title").exists());
        }

        @Test
        @DisplayName("E2E api book - POST /books - deve retornar 201")
        public void shouldCreateBook() throws Exception {
                BookDTO book = new BookDTO(
                                "Clean code",
                                "Robert .C Martin",
                                "Altabooks",
                                "TI",
                                ReadingStatus.READ);

                String json = objectMapper.writeValueAsString(book);

                mvc.perform(post("/books")
                                .with(user(userSystem))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isCreated());
        }

        @Test
        @DisplayName("E2E api book - GET /books com filtro - deve retornar apenas livros READ")
        public void shouldReturnReadBookList() throws Exception {

                mvc.perform(get("/books")
                                .with(user(userSystem))
                                .param("status", "READ"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].status").value("READ"));
        }

        @Test
        @DisplayName("E2E api book - GET /books com filtro - deve retornar apenas livros READING")
        public void shouldReturnReadingBookList() throws Exception {

                mvc.perform(get("/books")
                                .with(user(userSystem))
                                .param("status", "READING"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].status").value("READING"));
        }

        @Test
        @DisplayName("E2E api book - GET /books com filtro - deve retornar apenas livros WANNA_READ")
        public void shouldReturnWannaReadBookList() throws Exception {

                mvc.perform(get("/books")
                                .with(user(userSystem))
                                .param("status", "WANNA_READ"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.length()").value(1))
                                .andExpect(jsonPath("$[0].status").value("WANNA_READ"));
        }

        @Test
        @DisplayName("E2E api book - PUT /books - deve atualizar o livro retornando ok")
        public void shouldUpdateBook() throws Exception {
                List<BookDTOGet> books = bookService.getBooksByUserId(userIdTest);
                BookDTO newBook = new BookDTO(
                                "Clean code",
                                "Robert .C Martin",
                                "Altabooks",
                                "TI",
                                ReadingStatus.READ);

                String idBook = books.get(0).id();

                String json = objectMapper.writeValueAsString(newBook);

                mvc.perform(put("/books/" + idBook)
                                .with(user(userSystem))
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                                .andExpect(status().isOk());
        }

        @Test
        @DisplayName("E2E api book - DELETE /books - deve apagar o livro retornando noContent")
        public void shouldDeleteBook() throws Exception {
                List<BookDTOGet> books = bookService.getBooksByUserId(userIdTest);

                String idBook = books.get(0).id();

                mvc.perform(delete("/books/" + idBook)
                                .with(user(userSystem)))
                                .andExpect(status().isNoContent());
        }
}
