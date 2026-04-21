package com.projetogs.mylibrary.controller;

import com.projetogs.mylibrary.dto.BookDTO;
import com.projetogs.mylibrary.enums.ReadingStatus;
import com.projetogs.mylibrary.security.UserSystem;
import com.projetogs.mylibrary.service.BookService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/books")
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "bearer-jwt")
public class BookController {
    @Autowired
    private BookService bookService;

    @GetMapping()
    public ResponseEntity<List<BookDTO>> getBooksByUserId(@RequestParam(required = false) ReadingStatus status, Authentication authentication){
        UserSystem user = (UserSystem) authentication.getPrincipal();
        String id = user.getId();
        if (status != null){
            return ResponseEntity.ok(bookService.getBooksByUserIdAndStatus(id,status));
        }
        return ResponseEntity.ok(bookService.getBooksByUserId(id));
    }

    @PostMapping()
    public ResponseEntity<BookDTO> createBook (@RequestBody BookDTO dto, Authentication authentication){
        UserSystem user = (UserSystem) authentication.getPrincipal();
        String id = user.getId();
        BookDTO created = bookService.createBook(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{bookId}")
    public ResponseEntity<BookDTO> updateBook (@PathVariable String bookId, @RequestBody BookDTO dto, Authentication authentication){
        UserSystem user = (UserSystem) authentication.getPrincipal();
        String id = user.getId();
        BookDTO updated = bookService.updateBook(id, bookId, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{bookId}")
    public ResponseEntity<Void> deleteBook (@PathVariable String bookId, Authentication authentication){
        UserSystem user = (UserSystem) authentication.getPrincipal();
        String id = user.getId();
       bookService.deleteBook(id, bookId);
       return ResponseEntity.noContent().build();
    }
}
