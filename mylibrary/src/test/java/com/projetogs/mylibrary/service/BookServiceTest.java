package com.projetogs.mylibrary.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.bson.types.ObjectId;
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
import com.projetogs.mylibrary.enums.ReadingStatus;

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
