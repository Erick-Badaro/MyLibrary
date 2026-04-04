package com.projetogs.mylibrary.service;

import com.projetogs.mylibrary.dto.BookDTO;
import com.projetogs.mylibrary.entities.Book;
import com.projetogs.mylibrary.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookService {
@Autowired
   private BookRepository bookRepository;

    public List<BookDTO> getBooksByUserId(String userId) {
        return bookRepository.findByUserId(userId)
                .stream()
                .map(book -> new BookDTO(
                        book.getTitle(),
                        book.getAuthor(),
                        book.getGenre(),
                        book.getPublisher(),
                        book.getStatus()
                ))
                .toList();
    }
}
