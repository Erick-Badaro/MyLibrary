package com.projetogs.mylibrary.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.projetogs.mylibrary.config.MongoTestContainerConfig;
import com.projetogs.mylibrary.dto.BookDTO;
import com.projetogs.mylibrary.enums.ReadingStatus;

public class BookServiceTest extends MongoTestContainerConfig {

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
