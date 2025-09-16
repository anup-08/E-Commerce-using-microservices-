package com.authServer.controller;

import com.authServer.service.UserService;
import com.example.dtoRequest.UserRequest;
import com.example.dtoResponse.UserResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/auth")
public class Controller {

    private final UserService service;

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request){
        return ResponseEntity.ok(service.registerUser(request));
    }
}
