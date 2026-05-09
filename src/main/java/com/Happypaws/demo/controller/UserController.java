package com.Happypaws.demo.controller;
import com.Happypaws.demo.model.User;
import com.Happypaws.demo.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Users")
public class UserController {

    private final UserRepository repository;

    public UserController(UserRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<User> listar() {
        return repository.findAll();
    }

    @PostMapping
    public User guardar(@RequestBody User User) {
        return repository.save(User);
    }
}
