package com.example.aop.controller;

import com.example.aop.dto.UserDto;
import com.example.aop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestParam String name,
                                               @RequestParam String email) {
        return ResponseEntity.ok(userService.createUser(name, email));
    }

    @GetMapping("/error")
    public ResponseEntity<Void> triggerError() {
        userService.failingMethod();
        return ResponseEntity.ok().build();
    }
}
