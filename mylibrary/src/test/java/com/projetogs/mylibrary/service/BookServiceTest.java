package com.projetogs.mylibrary.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.projetogs.mylibrary.dto.BookDTO;
import com.projetogs.mylibrary.dto.BookDTOGet;
import com.projetogs.mylibrary.enums.ReadingStatus;
import com.projetogs.mylibrary.repository.BookRepository;

@SpringBootTest(properties = {
        "api.security.token.secret=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWUsImlhdCI6MTUxNjIzOTAyMn0.KMUFsIDTnFmyG3nMiGM6H9FNFUROf3wh7SmqJp-QV30"
})
@Testcontainers
@ActiveProfiles("test")
public class BookServiceTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

        registry.add("spring.data.mongodb.database", () -> "mylibrarytest");
    }

    @Autowired
    private BookService service;

    @Autowired
    private BookRepository repository;

    private String userIdTest;

    @BeforeEach
    public void setUp() {
        userIdTest = new ObjectId().toHexString();
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

        service.createBook(userIdTest, livroParaLer);
        service.createBook(userIdTest, livroLendo);
        service.createBook(userIdTest, livroLido);
    }

    @AfterEach
    public void tearDown() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Deve buscar os livros de um usuário no banco")
    public void testGetBooksByUserId() {
        List<BookDTOGet> result = service.getBooksByUserId(userIdTest);
        assertTrue(!result.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando o usuário não possui livros")
    public void testGetBooksByUserId_NotFound() {
        String idSemLivros = new ObjectId().toHexString();

        List<BookDTOGet> result = service.getBooksByUserId(idSemLivros);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Deve buscar os livros de um usuário pelo id e pelo status de leitura no banco")
    public void testGetBooksByUserIdAndStatus() {
        List<BookDTOGet> resultRead = service.getBooksByUserIdAndStatus(userIdTest, ReadingStatus.READ);
        List<BookDTOGet> resultReading = service.getBooksByUserIdAndStatus(userIdTest, ReadingStatus.READING);
        List<BookDTOGet> resultWannaRead = service.getBooksByUserIdAndStatus(userIdTest, ReadingStatus.WANNA_READ);
        assertTrue(!resultRead.isEmpty());
        assertTrue(!resultReading.isEmpty());
        assertTrue(!resultWannaRead.isEmpty());
    }

    @Test
    @DisplayName("Deve criar um livro relacionado a um usuário")
    public void testCreateBook() {
        BookDTO book = new BookDTO("Código limpo", "Robert C. Martin", "AltaBooks", "TI", ReadingStatus.READING);
        BookDTO saved = service.createBook(userIdTest, book);
        assertNotNull(saved);
    }

    @Test
    @DisplayName("Deve atualizar um livro passo pelo id")
    public void testIUpdateBook() {
        List<BookDTOGet> books = service.getBooksByUserId(userIdTest);
        String bookId = books.get(0).id();

        BookDTO update = new BookDTO("Arquitetura limpa", "Robert C. Martin", "Altabooks", "TI", ReadingStatus.READ);

        BookDTO saved = service.updateBook(userIdTest, bookId, update);

        assertNotNull(saved);
        assertEquals("Arquitetura limpa", saved.title(), "O título deveria ter sido atualizado");
        assertEquals("Robert C. Martin", saved.author(), "O autor deveria ter sido atualizado");
        assertEquals(ReadingStatus.READ, saved.status(), "O status deveria ser READ");
    }

    @Test
    @DisplayName("Deve deletar um livro passo pelo id do usuário e do livro")
    public void testDeleteBook() {
        List<BookDTOGet> books = service.getBooksByUserId(userIdTest);
        String bookId = books.get(0).id();

        service.deleteBook(userIdTest, bookId);
        List<BookDTOGet> result = service.getBooksByUserId(userIdTest);

        boolean isNotDelete = result.stream().anyMatch(b -> b.id().equals(bookId));

        assertFalse(isNotDelete, "O livro ainda existe no banco de dados");
    }
}
