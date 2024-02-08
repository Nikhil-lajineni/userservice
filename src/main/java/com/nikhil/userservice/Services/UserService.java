package com.nikhil.userservice.Services;

import com.nikhil.userservice.Models.Token;
import com.nikhil.userservice.Models.User;
import com.nikhil.userservice.Repo.TokenRepository;
import com.nikhil.userservice.Repo.UserRepository;
import lombok.NonNull;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private  TokenRepository tokenRepository;

    UserService(UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder,
                TokenRepository tokenRepository){
        this.userRepository=userRepository;
        this.bCryptPasswordEncoder=bCryptPasswordEncoder;
        this.tokenRepository = tokenRepository;
    }
    public User signup(String name, String password, String email) {
        User user=new User();
        user.setName(name);
        user.setEmail(email);
        BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
        String hashedPassword = bCryptPasswordEncoder.encode(password);
        user.setHashedPassword(hashedPassword);
        userRepository.save(user);
        return user;
    }

    public Token login(String email, String password) {
        Optional<User> user=userRepository.findByEmail(email);
        if(user.isEmpty()){
            //throw user not exists exception
            return null;
        }
        User newUser=user.get();
        if(!bCryptPasswordEncoder.matches(password, newUser.getHashedPassword())){
            //throw password exception
            return null;
        }
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plus(30, ChronoUnit.DAYS);

        // Convert LocalDate to Date
        Date expiryDate = Date.from(thirtyDaysLater.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Token token=new Token();
        token.setUser(newUser);
        token.setDeleted(false);
        token.setExpiryAt(expiryDate);
        token.setValue(RandomStringUtils.randomAlphanumeric(128));
        Token savedToken = tokenRepository.save(token);
        return savedToken;
    }

    public void logout(String token) {
        Optional<Token> tokenOptional=tokenRepository.findByValueAndDeletedEquals(token,false);
        if(tokenOptional.isEmpty()){
            //throw token not found exception
            return;
        }
        Token token1=tokenOptional.get();
        token1.setDeleted(true);
        tokenRepository.save(token1);
    }

    public User validateUser(String token){
        Optional<Token> tokenOptional=tokenRepository.findByValueAndDeletedEqualsAndExpiryAtGreaterThan(token,false,new Date());
        if(tokenOptional.isEmpty()){
            //throw token not found exception
            return null;
        }
        Token token1=tokenOptional.get();
        return token1.getUser();
    }
}
