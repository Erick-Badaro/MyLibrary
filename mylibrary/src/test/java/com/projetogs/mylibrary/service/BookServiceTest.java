package com.projetogs.mylibrary.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.projetogs.mylibrary.dto.BookDTO;
import com.projetogs.mylibrary.enums.ReadingStatus;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public class BookServiceTest {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7");

    @Autowired
    private BookService service;

    private String userIdTest = new ObjectId().toHexString();

    @Test
    @DisplayName("Deve buscar os livros de um usuário no banco")
    public void getBooksByUserId() {
        BookDTO novoLivro = new BookDTO("O Hobbit", "J.R.R. Tolkien", "Fantasia", "HarperCollins", ReadingStatus.READ);
        service.createBook(userIdTest, novoLivro);
        List<BookDTO> result = service.getBooksByUserId(userIdTest);
        assertTrue(!result.isEmpty());
    }
}
