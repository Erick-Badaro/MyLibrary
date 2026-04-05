package com.projetogs.mylibrary.service;

import com.projetogs.mylibrary.dto.BookDTO;
import com.projetogs.mylibrary.entities.Book;
import com.projetogs.mylibrary.enums.ReadingStatus;
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
                .map(this::toDTO)
                .toList();
    }

    public List<BookDTO> getBooksByUserIdAndStatus(String userId, ReadingStatus status){
        return bookRepository.findByUserIdAndStatus(userId, status)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    public BookDTO createBook(String userId, BookDTO dto){
        Book book = new Book();
        book.setUserId(userId);
        book.setTitle(dto.title());
        book.setAuthor(dto.author());
        book.setPublisher(dto.publisher());
        book.setGenre(dto.genre());
        book.setStatus(dto.status());

        Book saved = bookRepository.save(book);
        return toDTO(saved);
    }

    private BookDTO toDTO(Book book) {
        return new BookDTO(
                book.getTitle(),
                book.getAuthor(),
                book.getGenre(),
                book.getPublisher(),
                book.getStatus()
        );
    }
}

