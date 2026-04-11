package com.projetogs.mylibrary.repository;

import com.projetogs.mylibrary.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
