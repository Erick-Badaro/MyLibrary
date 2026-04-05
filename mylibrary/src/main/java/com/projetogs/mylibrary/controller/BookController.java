package com.projetogs.mylibrary.controller;

import com.projetogs.mylibrary.dto.BookDTO;
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

    @PutMapping("/{userId}/{bookId}")
    public ResponseEntity<BookDTO> updateBook (@PathVariable String userId, @PathVariable String bookId, @RequestBody BookDTO dto){
        BookDTO updated = bookService.updateBook(userId, bookId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{userId}/{bookId}")
    public ResponseEntity<Void> deleteBook (@PathVariable String userId, @PathVariable String bookId){
       bookService.deleteBook(userId, bookId);
       return ResponseEntity.noContent().build();
    }
}
