package com.nikhil.userservice.Controllers;


import com.nikhil.userservice.DTO.LoginRequestDto;
import com.nikhil.userservice.DTO.LogoutRequestDto;
import com.nikhil.userservice.DTO.SignUpRequestDto;
import com.nikhil.userservice.DTO.UserDto;
import com.nikhil.userservice.Models.Token;
import com.nikhil.userservice.Models.User;
import com.nikhil.userservice.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public Token login(@RequestBody LoginRequestDto request) {
        // check if email and password in db
        // if yes return user
        // else throw some error
        Token token=userService.login(request.getEmail(),request.getPassword());
        return token;

    }

    @PostMapping("/signup")
    public UserDto signUp(@RequestBody SignUpRequestDto request) {
        // no need to hash password for now
        // just store user as is in the db
        // for now no need to have email verification either
       User user= userService.signup(request.getName(),request.getPassword(),request.getEmail()
        );
        UserDto userDto=new UserDto();
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        userDto.setPassword(user.getHashedPassword());
        return userDto;

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequestDto request) {
        // delete token if exists -> 200
        // if doesn't exist give a 404
        userService.logout(request.getToken());

        return new ResponseEntity<>(HttpStatus.OK);
    }
}