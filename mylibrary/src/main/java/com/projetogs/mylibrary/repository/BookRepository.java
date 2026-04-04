package com.projetogs.mylibrary.repository;

import com.projetogs.mylibrary.entities.Book;
import com.projetogs.mylibrary.enums.ReadingStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookRepository extends MongoRepository <Book, String> {
    List<Book> findByUserId(String userId);
    List<Book> findByUserIdAndStatus(String userId, ReadingStatus status);
}
