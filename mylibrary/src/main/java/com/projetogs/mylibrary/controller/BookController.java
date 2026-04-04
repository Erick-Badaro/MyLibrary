package com.projetogs.mylibrary.controller;

import com.projetogs.mylibrary.dto.BookDTO;
import com.projetogs.mylibrary.entities.Book;
import com.projetogs.mylibrary.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<List<BookDTO>> getBooksByUserId(@PathVariable String id){
        List<BookDTO> books = bookService.getBooksByUserId(id);
        return ResponseEntity.ok(books);
    }

}
