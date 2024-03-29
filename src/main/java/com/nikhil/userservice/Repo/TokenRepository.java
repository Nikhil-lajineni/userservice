package com.nikhil.userservice.Repo;

import com.nikhil.userservice.Models.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token,Long> {
    Token save(Token token);
    Optional<Token> findByValueAndDeletedEquals(String token, boolean deleted);
    Optional<Token> findByValueAndDeletedEqualsAndExpiryAtGreaterThan(String token, boolean deleted, java.util.Date date);

}
