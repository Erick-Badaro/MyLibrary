package com.projetogs.mylibrary.controller;

import com.projetogs.mylibrary.dto.BookDTO;
import com.projetogs.mylibrary.entities.Book;
import com.projetogs.mylibrary.enums.ReadingStatus;
import com.projetogs.mylibrary.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping("/{id}")
    public ResponseEntity<List<BookDTO>> getBooksByUserId(@PathVariable String id, @RequestParam(required = false) ReadingStatus status){
        if (status != null){
            return ResponseEntity.ok(bookService.getBooksByUserIdAndStatus(id,status));
        }
        return ResponseEntity.ok(bookService.getBooksByUserId(id));
    }

    @PostMapping("/{userId}")
    public ResponseEntity<BookDTO> createBook (@PathVariable String userId, @RequestBody BookDTO dto){
        BookDTO created = bookService.createBook(userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}
